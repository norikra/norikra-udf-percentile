# Norikra::UDF::Percentile

This is Norikra UDF plugin to add function `percentile()` and `percentiles()`.

## Installation

Install on JRuby, which runs Norikra.

    $ gem intall norikra-udf-percentile

## Usage

### percentile( expression, int )

Write queries to get percentile as aggregate functions (like `min(), max(), count()`).

```sql
SELECT
  percentile( num_field, 90 ) AS p90,
  percentile( num_field, 95 ) AS p95
FROM
  test_target.win:time_batch(5 min)
```

And get output.

```json
{
  "p90":9.2,
  "p95":9.8
}
```

### percentiles( expression, int[] )

`percentiles()` is more efficient on CPU/memory than 2 or more times of `percentile()`. Use int-array primitive by `{}` brackets for second argument.

```sql
SELECT
  percentiles( num_field, {90, 95, 98, 99} ) AS percentiles
FROM
  test_target.win:time_batch(5 min)
```

And get output as nested object.

```json
{
  "percentiles": {
    "90": 9.2,
    "95": 9.8,
    "98": 9.9,
    "99": 9.9,
  }
}
```

## Copyright

* Copyright (c) 2013- TAGOMORI Satoshi (tagomoris)
* License
  * GPL v2
