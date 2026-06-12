terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
  }
}

provider "aws" {
  region = var.aws_region
}

# Find the latest Ubuntu 22.04 AMI
data "aws_ami" "ubuntu" {
  most_recent = true

  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-jammy-22.04-amd64-server-*"]
  }

  filter {
    name   = "virtualization-type"
    values = ["hvm"]
  }

  owners = ["099720109477"] # Canonical
}

# Security Group
resource "aws_security_group" "tenvia_sg" {
  name        = "tenvia_sg"
  description = "Allow inbound traffic for Tenvia app"

  ingress {
    description = "SSH"
    from_port   = 22
    to_port     = 22
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTP for Lets Encrypt"
    from_port   = 80
    to_port     = 80
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "HTTPS for Caddy"
    from_port   = 443
    to_port     = 443
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Tenvia App HTTP (Internal/Direct)"
    from_port   = 8080
    to_port     = 8080
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Leaderboard Service"
    from_port   = 8081
    to_port     = 8081
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  ingress {
    description = "Grafana"
    from_port   = 3000
    to_port     = 3000
    protocol    = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
  }

  egress {
    from_port   = 0
    to_port     = 0
    protocol    = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "Tenvia-SG"
  }
}

# EC2 Instance
resource "aws_instance" "tenvia_server" {
  ami           = data.aws_ami.ubuntu.id
  instance_type = var.instance_type

  vpc_security_group_ids = [aws_security_group.tenvia_sg.id]

  user_data = templatefile("${path.module}/user_data.sh.tpl", {
    domain_name = var.domain_name
  })

  tags = {
    Name = "Tenvia-Playtest-Server"
  }
}

# Route53 Zone
data "aws_route53_zone" "primary" {
  name         = var.domain_name
  private_zone = false
}

# DNS Record for the API
resource "aws_route53_record" "api" {
  zone_id = data.aws_route53_zone.primary.zone_id
  name    = "api.${var.domain_name}"
  type    = "A"
  ttl     = 300
  records = [aws_instance.tenvia_server.public_ip]
}

# DNS Record for the Leaderboard
resource "aws_route53_record" "leaderboard" {
  zone_id = data.aws_route53_zone.primary.zone_id
  name    = "leaderboard.${var.domain_name}"
  type    = "A"
  ttl     = 300
  records = [aws_instance.tenvia_server.public_ip]
}
