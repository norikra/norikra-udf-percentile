package is.tagomor.norikra.udf;

import com.espertech.esper.client.hook.AggregationFunctionFactory;
import com.espertech.esper.epl.agg.service.AggregationValidationContext;
import com.espertech.esper.epl.agg.aggregator.AggregationMethod;

import java.util.HashMap;

public class PercentilesFactory implements AggregationFunctionFactory {
  public void setFunctionName(String functionName) {
    // no action taken
  }

  /*
    percentiles(field_name, {percentile_specifications_array}[, samples])
    ex: percentiles(response_time, {50,90,95,98,99})
   */
  public void validate(AggregationValidationContext validationContext) {
    if (validationContext.getParameterTypes().length != 2)
      throw new IllegalArgumentException("percentiles() accepts 2 arguments (fieldname,percentiles)");

    Class first = validationContext.getParameterTypes()[0];
    if (! (first == int.class || first == long.class || first == double.class || first == float.class ||
           first == Integer.class || first == Long.class || first == Double.class || first == Float.class ||
           first == String.class))
      throw new IllegalArgumentException("percentiles() first argument must be string or number, but " + first.toString());

    Class second = validationContext.getParameterTypes()[1];
    boolean f_second = validationContext.getIsConstantValue()[1];
    if (second != Integer[].class || !f_second)
      throw new IllegalArgumentException("percentiles() second argument must be Constant Array of Integer");

    if (validationContext.isDistinct())
      throw new IllegalArgumentException("percentiles() doesn't permit DISTINCT modifier");
  }

  /*
    In order for the engine to validate the type returned by the aggregation function against
    the types expected by enclosing expressions, the getValueType must return the result type
    of any values produced by the aggregation function:
   */
  public Class getValueType() {
    return HashMap.class;
  }

  /*
    Finally the factory implementation must provide a newAggregator method that returns instances
    of AggregationMethod. The engine invokes this method for each new aggregation state to be allocated.
   */
  public AggregationMethod newAggregator() {
    return new Percentiles();
  }
}
