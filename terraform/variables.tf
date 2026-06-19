variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-west-2"
}

variable "instance_type" {
  description = "EC2 instance type"
  type        = string
  default     = "t3.medium"
}

variable "domain_name" {
  description = "The Route53 domain name"
  type        = string
  default     = "tenviagame.com"
}

variable "server_running" {
  description = "Set to true to spin up the EC2 server, false to destroy it while keeping the EFS drive"
  type        = bool
  default     = true
}
