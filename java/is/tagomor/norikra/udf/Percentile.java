package is.tagomor.norikra.udf;

import com.espertech.esper.epl.agg.aggregator.AggregationMethod;

import java.util.ArrayList;
import java.util.Random;

public class Percentile implements AggregationMethod {
  private long sampleSize;
  private ArrayList<Double> samples;
  private Integer[] targets;

  public Percentile() {
    sampleSize = 1000000; // default
    samples = null;
  }

  public long sampleSize() {
    return sampleSize;
  }
    
  public long sampleNum() {
    return samples.size();
  }

  public Class getValueType() {
    return Double[].class;
  }

  public void enter(Object value) {
    if (value == null)
      return;

    // ArrayList is not thread safe...

    Object[] objs = (Object[]) value;
    if (samples == null) {
      targets = (Integer[]) objs[1];

      long size = sampleSize;
      if (objs.length == 3)
        sampleSize = ((Long) objs[2]).longValue();
      samples = new ArrayList<Double>((int) sampleSize);
    }

    if (samples.size() >= sampleSize) {
      synchronized (samples) {
        reSample();
      }
    }
    
    Object v = objs[0];
    Double d;

    if (v.getClass() == Double.class)
      d = (Double) v;
    else if (v.getClass() == Float.class)
      d = new Double( ((Float) v).doubleValue() );
    else if (v.getClass() == Integer.class)
      d = new Double( ((Integer) v).doubleValue() );
    else if (v.getClass() == Long.class)
      d = new Double( ((Long) v).doubleValue() );
    else
      d = new Double((String) v);

    synchronized (samples) {
      samples.add(d);
    }
  }

  private void reSample() {
    long newSize = sampleSize / 2;
    Random rand = new Random();
    
    while(samples.size() > newSize) {
      samples.remove(rand.nextInt(samples.size()));
    }
  }

  public void leave(Object value) {
    // this function does nothing for leave...
  }

  public Object getValue() {
    ArrayList<Double> percentiles = new ArrayList<Double>(targets.length);
    Double[] values = (Double[]) samples.toArray(new Double[]{});
    java.util.Arrays.sort(values);
    int size = values.length;

    for (int i = 0 ; i < targets.length ; i += 1) {
      percentiles.add(values[size * targets[i] / 100]);
    }

    return (Double[]) percentiles.toArray(new Double[]{});
  }

  public void clear() {
    sampleSize = 1000000; // default
    samples = null;
  }
}
