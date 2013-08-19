package is.tagomor.norikra.udf;

import com.espertech.esper.epl.agg.aggregator.AggregationMethod;

import java.util.ArrayList;
import java.util.Random;

public class Percentiles implements AggregationMethod {
  private static int initSize = 100000;

  private ArrayList<Double> valueList;
  private Integer[] targets;

  private Double[] last;

  public Percentiles() {
    valueList = null;
    last = null;
  }

  public long valueListSize() {
    return valueList.size();
  }

  public Class getValueType() {
    return Double[].class;
  }

  public Double convertValue(Object v) {
    if (v.getClass() == Double.class)
      return (Double) v;
    else if (v.getClass() == Float.class)
      return new Double( ((Float) v).doubleValue() );
    else if (v.getClass() == Integer.class)
      return new Double( ((Integer) v).doubleValue() );
    else if (v.getClass() == Long.class)
      return new Double( ((Long) v).doubleValue() );
    else
      return new Double((String) v);
  }    

  public void enter(Object value) {
    if (value == null)
      return;

    Object[] objs = (Object[]) value;
    if (valueList == null) {
      targets = (Integer[]) objs[1];
      valueList = new ArrayList<Double>(initSize);
    }

    Double d = convertValue(objs[0]);
    synchronized (valueList) {
      valueList.add(d);
    }
  }

  public void leave(Object value) {
    if (value == null)
      return;

    Object[] objs = (Object[]) value;
    Double d = convertValue(objs[0]);
    synchronized (valueList) {
      valueList.remove(d);
    }
  }

  public Object getValue() {
    if (valueList.size() == 0)
      return last;

    ArrayList<Double> percentiles = new ArrayList<Double>(targets.length);
    Double[] values = (Double[]) valueList.toArray(new Double[]{});

    // for thread safety
    if (values.length == 0)
      return last;

    java.util.Arrays.sort(values);
    int size = values.length;

    for (int i = 0 ; i < targets.length ; i += 1) {
      percentiles.add(values[size * targets[i] / 100]);
    }

    last = (Double[]) percentiles.toArray(new Double[]{});
    return last;
  }

  public void clear() {
    valueList = null;
  }
}
