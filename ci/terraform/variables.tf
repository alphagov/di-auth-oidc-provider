variable "lambda-zip-file" {
  default     = "../../serverless/lambda/build/distributions/lambda.zip"
  description = "Location of the Lambda ZIP file"
  type        = string
}

variable "aws_account" {
  default = "761723964695"
  type    = string
}

variable "deployer_role" {
  default     = "concourse-deployer"
  description = "The name of the AWS role to assume, when running locally use developer admin role (i.e. '<firstname>.<surname>-admin'"
  type        = string
}
