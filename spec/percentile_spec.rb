require 'norikra/udf_spec_helper'

include Norikra::UDFSpecHelper

require 'norikra/udf/percentile'

describe Norikra::UDF::Percentile do
  f = udf_function(
    Norikra::UDF::Percentile,
    :valueType => java.lang.Double,
    :parameters => [
      [java.lang.String, false],
      [java.lang.Integer, true, 50],
    ]
  )

  it 'returns Double values' do
    expect(f.getValueType).to eql(java.lang.Double.java_class)
  end

  it 'returns 9 values as percentiles, and reflect for leve()' do
    (0...100).each do |i|
      f._call(:enter, [i.to_s, 50.to_java(:Integer)].to_java)
    end

    r = f.getValue
    expect(r).to eql(50.0)

    (0...50).each do |i|
      f._call(:leave, [i.to_s, 50.to_java(:Integer)].to_java)
    end

    expect(f.instance_eval{ @func.valueListSize }).to eql(50)

    r = f.getValue
    expect(r).to eql(75.0)
  end

  f2 = udf_function(
    Norikra::UDF::Percentile,
    :valueType => java.lang.Double,
    :parameters => [
      [java.lang.Integer, false],
      [java.lang.Integer, true, 90],
    ]
  )
  it 'returns 5 values as percentiles, and returns same value after all values leaving' do
    (0...2000).to_a.shuffle.each do |i|
      f2._call(:enter, [i, 90.to_java(:Integer)].to_java)
    end

    expect(f2.instance_eval{ @func.valueListSize }).to eql(2000)

    r = f2.getValue
    expect(r).to eql(1800.0)

    (0...2000).to_a.shuffle.each do |i|
      f2._call(:leave, [i, 90.to_java(:Integer)].to_java)
    end

    r2 = f2.getValue
    expect(r2).to eql(r)
  end
end
