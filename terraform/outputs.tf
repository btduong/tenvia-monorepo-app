output "public_ip" {
  description = "Public IP address of the EC2 instance"
  value       = join("", aws_instance.tenvia_server[*].public_ip)
}

output "app_url" {
  description = "URL to access the Tenvia app"
  value       = length(aws_instance.tenvia_server) > 0 ? "http://${aws_instance.tenvia_server[0].public_ip}:8080" : ""
}

output "grafana_url" {
  description = "URL to access Grafana"
  value       = length(aws_instance.tenvia_server) > 0 ? "http://${aws_instance.tenvia_server[0].public_ip}:3000" : ""
}

output "api_url" {
  description = "The permanent API URL using the Route53 domain"
  value       = "https://api.${var.domain_name}"
}

output "leaderboard_api_url" {
  description = "The permanent Leaderboard API URL using the Route53 domain"
  value       = "https://leaderboard.${var.domain_name}"
}
