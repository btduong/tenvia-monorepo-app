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
