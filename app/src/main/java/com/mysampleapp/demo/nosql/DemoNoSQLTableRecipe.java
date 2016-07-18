package com.mysampleapp.demo.nosql;

import android.content.Context;
import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.util.ThreadUtils;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.mysampleapp.R;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


public class DemoNoSQLTableRecipe extends DemoNoSQLTableBase {
    private static final String LOG_TAG = DemoNoSQLTableRecipe.class.getSimpleName();

    /** Inner classes use this value to determine how many results to retrieve per service call. */
    private static final int RESULTS_PER_RESULT_GROUP = 40;

    /** Removing sample data removes the items in batches of the following size. */
    private static final int MAX_BATCH_SIZE_FOR_DELETE = 50;


    /********* Primary Get Query Inner Classes *********/

    public class DemoGetWithPartitionKeyAndSortKey extends DemoNoSQLOperationBase {
        private RecipeDO result;
        private boolean resultRetrieved = true;

        DemoGetWithPartitionKeyAndSortKey(final Context context) {
            super(context.getString(R.string.nosql_operation_get_by_partition_and_sort_text),
                String.format(context.getString(R.string.nosql_operation_example_get_by_partition_and_sort_text),
                    "Name", "demo-Name-3",
                    "Difficulty", "1111500000"));
        }

        @Override
        public boolean executeOperation() {
            // Retrieve an item by passing the partition key using the object mapper.
            result = mapper.load(RecipeDO.class, "demo-Name-3", 1111500000.0);

            if (result != null) {
                resultRetrieved = false;
                return true;
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            if (resultRetrieved) {
                return null;
            }
            final List<DemoNoSQLResult> results = new ArrayList<>();
            results.add(new DemoNoSQLRecipeResult(result));
            resultRetrieved = true;
            return results;
        }

        @Override
        public void resetResults() {
            resultRetrieved = false;
        }
    }

    /* ******** Primary Index Query Inner Classes ******** */

    public class DemoQueryWithPartitionKeyAndSortKeyCondition extends DemoNoSQLOperationBase {

        private PaginatedQueryList<RecipeDO> results;
        private Iterator<RecipeDO> resultsIterator;

        DemoQueryWithPartitionKeyAndSortKeyCondition(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_and_sort_condition_text),
                  String.format(context.getString(R.string.nosql_operation_example_query_by_partition_and_sort_condition_text),
                      "Name", "demo-Name-3",
                      "Difficulty", "1111500000"));
        }

        @Override
        public boolean executeOperation() {
            final RecipeDO itemToFind = new RecipeDO();
            itemToFind.setName("demo-Name-3");

            final Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.LT.toString())
                .withAttributeValueList(new AttributeValue().withN(Double.toString(1111500000.0)));
            final DynamoDBQueryExpression<RecipeDO> queryExpression = new DynamoDBQueryExpression<RecipeDO>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("Difficulty", rangeKeyCondition)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(RecipeDO.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Gets the next page of results from the query.
         * @return list of results, or null if there are no more results.
         */
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoQueryWithPartitionKeyOnly extends DemoNoSQLOperationBase {

        private PaginatedQueryList<RecipeDO> results;
        private Iterator<RecipeDO> resultsIterator;

        DemoQueryWithPartitionKeyOnly(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_text),
                String.format(context.getString(R.string.nosql_operation_example_query_by_partition_text),
                    "Name", "demo-Name-3"));
        }

        @Override
        public boolean executeOperation() {
            final RecipeDO itemToFind = new RecipeDO();
            itemToFind.setName("demo-Name-3");

            final DynamoDBQueryExpression<RecipeDO> queryExpression = new DynamoDBQueryExpression<RecipeDO>()
                .withHashKeyValues(itemToFind)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(RecipeDO.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoQueryWithPartitionKeyAndFilter extends DemoNoSQLOperationBase {

        private PaginatedQueryList<RecipeDO> results;
        private Iterator<RecipeDO> resultsIterator;

        DemoQueryWithPartitionKeyAndFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_and_filter_text),
                  String.format(context.getString(R.string.nosql_operation_example_query_by_partition_and_filter_text),
                      "Name", "demo-Name-3",
                      "Advice", "demo-Advice-500000"));
        }

        @Override
        public boolean executeOperation() {
            final RecipeDO itemToFind = new RecipeDO();
            itemToFind.setName("demo-Name-3");

            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#Advice", "Advice");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":MinAdvice",
                new AttributeValue().withS("demo-Advice-500000"));

            final DynamoDBQueryExpression<RecipeDO> queryExpression = new DynamoDBQueryExpression<RecipeDO>()
                .withHashKeyValues(itemToFind)
                .withFilterExpression("#Advice > :MinAdvice")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(RecipeDO.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
             resultsIterator = results.iterator();
         }
    }

    public class DemoQueryWithPartitionKeySortKeyConditionAndFilter extends DemoNoSQLOperationBase {

        private PaginatedQueryList<RecipeDO> results;
        private Iterator<RecipeDO> resultsIterator;

        DemoQueryWithPartitionKeySortKeyConditionAndFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_query_by_partition_sort_condition_and_filter_text),
                  String.format(context.getString(R.string.nosql_operation_example_query_by_partition_sort_condition_and_filter_text),
                      "Name", "demo-Name-3",
                      "Difficulty", "1111500000",
                      "Advice", "demo-Advice-500000"));
        }

        public boolean executeOperation() {
            final RecipeDO itemToFind = new RecipeDO();
            itemToFind.setName("demo-Name-3");

            final Condition rangeKeyCondition = new Condition()
                .withComparisonOperator(ComparisonOperator.LT.toString())
                .withAttributeValueList(new AttributeValue().withN(Double.toString(1111500000.0)));

            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#Advice", "Advice");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":MinAdvice",
                new AttributeValue().withS("demo-Advice-500000"));

            final DynamoDBQueryExpression<RecipeDO> queryExpression = new DynamoDBQueryExpression<RecipeDO>()
                .withHashKeyValues(itemToFind)
                .withRangeKeyCondition("Difficulty", rangeKeyCondition)
                .withFilterExpression("#Advice > :MinAdvice")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues)
                .withConsistentRead(false)
                .withLimit(RESULTS_PER_RESULT_GROUP);

            results = mapper.query(RecipeDO.class, queryExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    /* ******** Secondary Named Index Query Inner Classes ******** */

    /********* Scan Inner Classes *********/

    public class DemoScanWithFilter extends DemoNoSQLOperationBase {

        private PaginatedScanList<RecipeDO> results;
        private Iterator<RecipeDO> resultsIterator;

        DemoScanWithFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_scan_with_filter),
                String.format(context.getString(R.string.nosql_operation_example_scan_with_filter),
                    "Advice", "demo-Advice-500000"));
        }

        @Override
        public boolean executeOperation() {
            // Use an expression names Map to avoid the potential for attribute names
            // colliding with DynamoDB reserved words.
            final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
            filterExpressionAttributeNames.put("#Advice", "Advice");

            final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
            filterExpressionAttributeValues.put(":MinAdvice",
                new AttributeValue().withS("demo-Advice-500000"));
            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("#Advice > :MinAdvice")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues);

            results = mapper.scan(RecipeDO.class, scanExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public boolean isScan() {
            return true;
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    public class DemoScanWithoutFilter extends DemoNoSQLOperationBase {

        private PaginatedScanList<RecipeDO> results;
        private Iterator<RecipeDO> resultsIterator;

        DemoScanWithoutFilter(final Context context) {
            super(context.getString(R.string.nosql_operation_title_scan_without_filter),
                context.getString(R.string.nosql_operation_example_scan_without_filter));
        }

        @Override
        public boolean executeOperation() {
            final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            results = mapper.scan(RecipeDO.class, scanExpression);
            if (results != null) {
                resultsIterator = results.iterator();
                if (resultsIterator.hasNext()) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public List<DemoNoSQLResult> getNextResultGroup() {
            return getNextResultsGroupFromIterator(resultsIterator);
        }

        @Override
        public boolean isScan() {
            return true;
        }

        @Override
        public void resetResults() {
            resultsIterator = results.iterator();
        }
    }

    /**
     * Helper Method to handle retrieving the next group of query results.
     * @param resultsIterator the iterator for all the results (makes a new service call for each result group).
     * @return the next list of results.
     */
    private static List<DemoNoSQLResult> getNextResultsGroupFromIterator(final Iterator<RecipeDO> resultsIterator) {
        if (!resultsIterator.hasNext()) {
            return null;
        }
        List<DemoNoSQLResult> resultGroup = new LinkedList<>();
        int itemsRetrieved = 0;
        do {
            // Retrieve the item from the paginated results.
            final RecipeDO item = resultsIterator.next();
            // Add the item to a group of results that will be displayed later.
            resultGroup.add(new DemoNoSQLRecipeResult(item));
            itemsRetrieved++;
        } while ((itemsRetrieved < RESULTS_PER_RESULT_GROUP) && resultsIterator.hasNext());
        return resultGroup;
    }

    /** The DynamoDB object mapper for accessing DynamoDB. */
    private final DynamoDBMapper mapper;

    public DemoNoSQLTableRecipe() {
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    }

    @Override
    public String getTableName() {
        return "Recipe";
    }

    @Override
    public String getPartitionKeyName() {
        return "Artist";
    }

    public String getPartitionKeyType() {
        return "String";
    }

    @Override
    public String getSortKeyName() {
        return "Difficulty";
    }

    public String getSortKeyType() {
        return "Number";
    }

    @Override
    public int getNumIndexes() {
        return 0;
    }

    @Override
    public void insertSampleData() throws AmazonClientException {
        Log.d(LOG_TAG, "Inserting Sample data.");
        final RecipeDO firstItem = new RecipeDO();

        firstItem.setName("demo-Name-3");
        firstItem.setDifficulty(1111500000.0);
        firstItem.setAdvice(
            DemoSampleDataGenerator.getRandomSampleString("Advice"));
        firstItem.setAmount(DemoSampleDataGenerator.getRandomSampleNumber());
        firstItem.setCookingTime(DemoSampleDataGenerator.getRandomSampleNumber());
        firstItem.setCountry(
            DemoSampleDataGenerator.getRandomSampleString("Country"));
        firstItem.setIntroduction(
            DemoSampleDataGenerator.getRandomSampleString("Introduction"));
        firstItem.setPreparationTime(DemoSampleDataGenerator.getRandomSampleNumber());
        firstItem.setType(
            DemoSampleDataGenerator.getRandomSampleString("Type"));
        firstItem.setVegetarian(DemoSampleDataGenerator.getRandomSampleBool());
        AmazonClientException lastException = null;

        try {
            mapper.save(firstItem);
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item : " + ex.getMessage(), ex);
            lastException = ex;
        }

        final RecipeDO[] items = new RecipeDO[SAMPLE_DATA_ENTRIES_PER_INSERT-1];
        for (int count = 0; count < SAMPLE_DATA_ENTRIES_PER_INSERT-1; count++) {
            final RecipeDO item = new RecipeDO();
            item.setName(DemoSampleDataGenerator.getRandomPartitionSampleString("Name"));
            item.setDifficulty(DemoSampleDataGenerator.getRandomSampleNumber());
            item.setAdvice(DemoSampleDataGenerator.getRandomSampleString("Advice"));
            item.setAmount(DemoSampleDataGenerator.getRandomSampleNumber());
            item.setCookingTime(DemoSampleDataGenerator.getRandomSampleNumber());
            item.setCountry(DemoSampleDataGenerator.getRandomSampleString("Country"));
            item.setIntroduction(DemoSampleDataGenerator.getRandomSampleString("Introduction"));
            item.setPreparationTime(DemoSampleDataGenerator.getRandomSampleNumber());
            item.setType(DemoSampleDataGenerator.getRandomSampleString("Type"));
            item.setVegetarian(DemoSampleDataGenerator.getRandomSampleBool());

            items[count] = item;
        }
        try {
            mapper.batchSave(Arrays.asList(items));
        } catch (final AmazonClientException ex) {
            Log.e(LOG_TAG, "Failed saving item batch : " + ex.getMessage(), ex);
            lastException = ex;
        }

        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            throw lastException;
        }
    }

    @Override
    public void removeSampleData() throws AmazonClientException {
        // Scan for the sample data to remove it.

        // Use an expression names Map to avoid the potential for attribute names
        // colliding with DynamoDB reserved words.
        final Map <String, String> filterExpressionAttributeNames = new HashMap<>();
        filterExpressionAttributeNames.put("#hashAttribute", "Name");

        final Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":prefixVal", new AttributeValue().withS(
            DemoSampleDataGenerator.SAMPLE_DATA_STRING_PREFIX));

        final String hashKeyFilterCondition = "begins_with(#hashAttribute, :prefixVal)";

        filterExpressionAttributeNames.put("#rangeAttribute","Difficulty");
        expressionAttributeValues.put(":startVal", new AttributeValue().withN("1111000000"));
        expressionAttributeValues.put(":endVal", new AttributeValue().withN("1111999999"));
        final String sortKeyFilterCondition = "#rangeAttribute BETWEEN :startVal AND :endVal";
        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
            .withFilterExpression(hashKeyFilterCondition + " and " + sortKeyFilterCondition)
            .withExpressionAttributeNames(filterExpressionAttributeNames)
            .withExpressionAttributeValues(expressionAttributeValues);

        PaginatedScanList<RecipeDO> results = mapper.scan(RecipeDO.class, scanExpression);

        Iterator<RecipeDO> resultsIterator = results.iterator();

        AmazonClientException lastException = null;

        if (resultsIterator.hasNext()) {
            final RecipeDO item = resultsIterator.next();

            // Demonstrate deleting a single item.
            try {
                mapper.delete(item);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item : " + ex.getMessage(), ex);
                lastException = ex;
            }
        }

        final List<RecipeDO> batchOfItems = new LinkedList<RecipeDO>();
        while (resultsIterator.hasNext()) {
            // Build a batch of books to delete.
            for (int index = 0; index < MAX_BATCH_SIZE_FOR_DELETE && resultsIterator.hasNext(); index++) {
                batchOfItems.add(resultsIterator.next());
            }
            try {
                // Delete a batch of items.
                mapper.batchDelete(batchOfItems);
            } catch (final AmazonClientException ex) {
                Log.e(LOG_TAG, "Failed deleting item batch : " + ex.getMessage(), ex);
                lastException = ex;
            }

            // clear the list for re-use.
            batchOfItems.clear();
        }


        if (lastException != null) {
            // Re-throw the last exception encountered to alert the user.
            // The logs contain all the exceptions that occurred during attempted delete.
            throw lastException;
        }
    }

    private List<DemoNoSQLOperationListItem> getSupportedDemoOperations(final Context context) {
        List<DemoNoSQLOperationListItem> noSQLOperationsList = new ArrayList<DemoNoSQLOperationListItem>();
        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_get)));
        noSQLOperationsList.add(new DemoGetWithPartitionKeyAndSortKey(context));

        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_primary_queries)));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeyOnly(context));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeyAndFilter(context));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeyAndSortKeyCondition(context));
        noSQLOperationsList.add(new DemoQueryWithPartitionKeySortKeyConditionAndFilter(context));

        noSQLOperationsList.add(new DemoNoSQLOperationListHeader(
            context.getString(R.string.nosql_operation_header_scan)));
        noSQLOperationsList.add(new DemoScanWithoutFilter(context));
        noSQLOperationsList.add(new DemoScanWithFilter(context));
        return noSQLOperationsList;
    }

    @Override
    public void getSupportedDemoOperations(final Context context,
                                           final SupportedDemoOperationsHandler opsHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<DemoNoSQLOperationListItem> supportedOperations = getSupportedDemoOperations(context);
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        opsHandler.onSupportedOperationsReceived(supportedOperations);
                    }
                });
            }
        }).start();
    }
}
