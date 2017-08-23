package com.github.terma.fastselect.callbacks;

import com.github.terma.fastselect.FastSelect;
import com.github.terma.fastselect.data.ByteData;
import com.github.terma.fastselect.data.Data;
import com.github.terma.fastselect.data.LongData;
import junit.framework.Assert;
import org.junit.Test;

import java.util.*;

@SuppressWarnings("unchecked")
public class AggregateCallbackTest {

    @Test
    public void empty() {
        Aggregator aggregator = new Aggregator() {
            @Override
            public void aggregate(Object agg, int position) {

            }

            @Override
            public Object create(int position) {
                return null;
            }
        };

        AggregateCallback aggregateCallback = new AggregateCallback(aggregator, new Data[0]);
        Assert.assertEquals(new HashMap(), aggregateCallback.getResult());
    }

    @Test
    public void aggregateByZeroColumnsMeansAggregateToOneRow() {
        Aggregator<List<Integer>> aggregator = new PositionsAggregator();

        AggregateCallback<List<Integer>> aggregateCallback = new AggregateCallback<>(aggregator, new Data[0]);
        aggregateCallback.data(0);
        aggregateCallback.data(1);
        aggregateCallback.data(3);

        Assert.assertEquals(new HashMap() {{
            put(new AggregateKey(new Data[0], 0), Arrays.asList(0, 1, 3));
        }}, aggregateCallback.getResult());
    }

    @Test
    public void shouldCallCreateOnceForNewUniqueKeyAndThenAggregate() {
        Aggregator<List<Integer>> aggregator = new PositionsAggregator();

        AggregateCallback<List<Integer>> aggregateCallback = new AggregateCallback<>(aggregator, new Data[0]);
        aggregateCallback.data(0);
        aggregateCallback.data(1);
        aggregateCallback.data(3);

        Assert.assertEquals(new HashMap() {{
            put(new AggregateKey(new Data[0], 0), Arrays.asList(0, 1, 3));
        }}, aggregateCallback.getResult());
    }

    @Test
    public void shouldAggregateByMultipleColumn() {
        Aggregator<List<Integer>> aggregator = new PositionsAggregator();

        final ByteData data1 = new ByteData(16);
        data1.add((byte) 1);
        data1.add((byte) 1);
        data1.add((byte) 10);
        data1.add((byte) 100);
        data1.add((byte) 100);
        final LongData data2 = new LongData(16);
        data2.add(-1);
        data2.add(-10);
        data2.add(-10);
        data2.add(-100);
        data2.add(-100);

        AggregateCallback<List<Integer>> aggregateCallback = new AggregateCallback<>(aggregator, data1, data2);
        for (int i = 0; i < data1.size(); i++) aggregateCallback.data(i);

        Assert.assertEquals(new HashMap() {{
            put(new AggregateKey(new Data[]{data1, data2}, 0), Collections.singletonList(0));
            put(new AggregateKey(new Data[]{data1, data2}, 3), Arrays.asList(3, 4));
            put(new AggregateKey(new Data[]{data1, data2}, 2), Collections.singletonList(2));
            put(new AggregateKey(new Data[]{data1, data2}, 1), Collections.singletonList(1));
        }}, aggregateCallback.getResult());
    }

    @Test(expected = NullPointerException.class)
    public void throwExceptionWhenCreateWithNullAggregator() {
        new AggregateCallback(null, new Data[0]);
    }

    @Test(expected = NullPointerException.class)
    public void throwExceptionWhenCreateWithNullAggregator1() {
        new AggregateCallback(null, new FastSelect.Column[0]);
    }

    private static class PositionsAggregator implements Aggregator<List<Integer>> {
        @Override
        public void aggregate(List<Integer> agg, int position) {
            agg.add(position);
        }

        @Override
        public List<Integer> create(int position) {
            ArrayList<Integer> list = new ArrayList<>();
            list.add(position);
            return list;
        }
    }
}
