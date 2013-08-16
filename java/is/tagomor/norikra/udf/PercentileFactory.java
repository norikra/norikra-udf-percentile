package is.tagomor.norikra.udf;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;

// import java.util.Map;

public class PercentileFactory implements AggregationFunctionFactory {
  public void setFunctionName(String functionName) {
    // no action taken
  }

  /*
    percentile(field_name, {percentile_specifications_array}[, samples])
    ex: percentile(response_time, {50,90,95,98,99})
        percentile(response_time, {50,90,95,98,99}, 10000)
   */
  public void validate(AggregationValidationContext validationContext) {
    if (validationContext.getParameterTypes().length != 2 && validationContext.getParameterTypes().length != 3)
      throw new IllegalArgumentException("percentile() accepts 2 or 3 arguments (fieldname,percentiles[,samples])");

    Class first = validationContext.getParameterTypes()[0];
    if (! (first == Integer.class || first == Long.class ||
           first == Double.class || first == Float.class || first == String.class))
      throw new IllegalArgumentException("percentile() first argument must be string or number");

    Class second = validationContext.getParameterTypes()[1];
    boolean f_second = validationContext.getIsConstantValue()[1];
    if (second != Integer[].class || !f_second)
      throw new IllegalArgumentException("percentile() second argument must be Constant Array of Integer");

    if (validationContext.getParameterTypes().length == 3) {
      Class third = validationContext.getParameterTypes()[2];
      boolean f_third = validationContext.getIsConstantValue()[2];
      if (third != Integer.class)
        throw new IllegalArgumentException("percentile() third argument must be Constant Integer");
    }
  }

  /*
    In order for the engine to validate the type returned by the aggregation function against
    the types expected by enclosing expressions, the getValueType must return the result type
    of any values produced by the aggregation function:
   */
  public Class getValueType() {
    return Double[].class;
  }

  /*
    Finally the factory implementation must provide a newAggregator method that returns instances
    of AggregationMethod. The engine invokes this method for each new aggregation state to be allocated.
   */
  public AggregationMethod newAggregator() {
    return new Percentile();
  }
}
