package com.home.amazon.serverless.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.home.amazon.serverless.model.Employee;
import com.home.amazon.serverless.utils.DependencyFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Collections;
import java.util.Map;

public class UpdateItemFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final int STATUS_CODE_NO_CONTENT = 204;
    static final int STATUS_CODE_CREATED = 201;
    static final int STATUS_CODE_BAD_REQUEST = 400;
    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<Employee> employeeTableSchema;

    public UpdateItemFunction() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();
        employeeTableSchema = TableSchema.fromBean(Employee.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String body = request.getBody();
        String responseBody = "";
        if (body != null && !body.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Employee item;
            try {
                item = objectMapper.readValue(body, Employee.class);
                if (item != null) {
                    DynamoDbTable<Employee> booksTable = dbClient.table(tableName, employeeTableSchema);
                    Employee updateResult = booksTable.updateItem(item);
                    responseBody = objectMapper.writeValueAsString(updateResult);
                }
            } catch (JsonProcessingException e) {
                context.getLogger().log("Failed to process JSON: " + e);
            }
        }
        return new APIGatewayProxyResponseEvent().withStatusCode(200)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap())
                .withBody(responseBody);
    }
}
