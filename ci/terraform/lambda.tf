resource "aws_iam_role" "iam_for_lambda" {
  name = "iam_for_lambda"

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow",
      "Sid": ""
    }
  ]
}
EOF
}

resource "aws_lambda_function" "test_lambda" {
  filename = "../../serverless/lambda/build/distributions/lambda.zip"
  function_name = "HelloWorldAPIGatewayLambda"
  role = aws_iam_role.iam_for_lambda.arn
  handler = "uk.gov.di.example.HelloWorldAPIGatewayLambda::handleRequest"

  source_code_hash = filebase64sha256("../../serverless/lambda/build/distributions/lambda.zip")

  runtime = "java11"

}

//resource "aws_lambda_permission" "apigw" {
//  statement_id  = "AllowAPIGatewayInvoke"
//  action        = "lambda:InvokeFunction"
//  function_name = aws_lambda_function.test_lambda.function_name
//  principal     = "apigateway.amazonaws.com"
//
//  # The "/*/*" portion grants access from any method on any resource
//  # within the API Gateway REST API.
//  source_arn = "${aws_api_gateway_rest_api.di-oidc-api.execution_arn}/*/*"
//}