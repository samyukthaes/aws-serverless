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

public class PostItemFunction implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    static final int STATUS_CODE_NO_CONTENT = 204;
    static final int STATUS_CODE_CREATED = 201;
    static final int STATUS_CODE_BAD_REQUEST = 400;
    private final DynamoDbEnhancedClient dbClient;
    private final String tableName;
    private final TableSchema<Employee> employeeTableSchema;

    public PostItemFunction() {
        dbClient = DependencyFactory.dynamoDbEnhancedClient();
        tableName = DependencyFactory.tableName();
        employeeTableSchema = TableSchema.fromBean(Employee.class);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        String body = request.getBody();
        int statusCode = STATUS_CODE_NO_CONTENT;
        if (body != null && !body.isEmpty()) {
            Employee item;
            try {
                item = new ObjectMapper().readValue(body, Employee.class);
                if (item != null) {
                    Map<String, String> pathParameters = request.getPathParameters();
                    if (arePathParametersValid(pathParameters, item)) {
                        DynamoDbTable<Employee> booksTable = dbClient.table(tableName, employeeTableSchema);
                        booksTable.putItem(item);
                        statusCode = STATUS_CODE_CREATED;
                    } else {
                        statusCode = STATUS_CODE_BAD_REQUEST;
                    }
                }
            } catch (JsonProcessingException e) {
                context.getLogger().log("Failed to deserialize JSON: " + e);
            }

        }
        return new APIGatewayProxyResponseEvent().withStatusCode(statusCode)
                .withIsBase64Encoded(Boolean.FALSE)
                .withHeaders(Collections.emptyMap());
    }

    private boolean arePathParametersValid(Map<String, String> pathParameters, Employee item) {
        if (pathParameters == null) {
            return false;
        }
        String itemPartitionKey = pathParameters.get(Employee.PARTITION_KEY);
        if (itemPartitionKey == null || itemPartitionKey.isEmpty()) {
            return false;
        }
        return itemPartitionKey.equals(item.getPsno());
    }
}