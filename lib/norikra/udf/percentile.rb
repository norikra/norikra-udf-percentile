require 'norikra/udf'

module Norikra
  module UDF
    class Percentile < Norikra::UDF::AggregationSingle
      def self.init
        require 'norikra-udf-percentile.jar'
      end
      def definition
        ["percentile", "is.tagomor.norikra.udf.PercentileFactory"]
      end
    end

    class Percentiles < Norikra::UDF::AggregationSingle
      def self.init
        require 'norikra-udf-percentile.jar'
      end
      def definition
        ["percentiles", "is.tagomor.norikra.udf.PercentilesFactory"]
      end
    end
  end
end
