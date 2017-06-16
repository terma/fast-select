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

### Get count with grouping
```java
MultiGroupCountCallback callback = new MultiGroupCountCallback(fastSelect.getColumnsByNames().get("a"));
database.select(
  new Request[] {new IntRequest("a", new int[]{12, 3})}, 
  callback);
callback.getCounters(); // your result here grouped by field 'a'
```

### Select first 25 items from sorted dataset
```java
ListLimitCallback<DemoData> callback = new ListLimitCallback<>(25);
fastSelect.selectAndSort(where, callback, "a");
callback.getResult();
```

### Filter data set get total and render only one page
```java
// get ref to real data
IntData id = (IntData) fastSelect.getColumnsByNames().get("id").data;

List<Integer> positions = fastSelect.selectPositions(new Request[] {...});
Collections.sort(positions, new Comparator<Integer>() {
    public int compare(Integer p1, Integer p2) { 
        return id.data[p1] - id.data[p2];
    }
});

// page render
List<Map<String, String>> page = new ArrayList<>();
for (int i = 10; i < 20; i++) {
    int p = positions.get(i);
    Map<String, String> row = new HashMap<>();
    row.put("id", id.data[p]);
    page.add(row);
}

int total = positions.size();
```

More use cases you can find in javadoc ```callbacks``` package
