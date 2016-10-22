## How to use

### Create Data Class

```java
public class Data {
    public byte a;
    public byte b;
}
```

### Build storage

```java
FastSelect<Data> database = new FastSelectBuilder<>(Data.class).create();

// add your data
database.addAll(new ArrayList<Data>(...)); 
```
### Create filter criteria 
```java
Request[] where = new Request[] {new IntRequest("a", new int[]{12, 3})};
```

### Get count with grouping
```java
MultiGroupCountCallback callback = new MultiGroupCountCallback(fastSelect.getColumnsByNames().get("a"));
database.select(where, callback);
callback.getCounters(); // your result here grouped by field 'a'
```

### Select with sorting
```java
ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
fastSelect.selectAndSort(where, callback, "a");
callback.getResult();
```

More use cases you can find in javadoc ```callbacks``` package
