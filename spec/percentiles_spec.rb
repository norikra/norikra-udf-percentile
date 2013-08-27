require 'norikra/udf_spec_helper'

include Norikra::UDFSpecHelper

require 'norikra/udf/percentile'

describe Norikra::UDF::Percentiles do
  f = udf_function(
    Norikra::UDF::Percentiles,
    :valueType => java.util.HashMap,
    :parameters => [
      [java.lang.String, false],
      [java.lang.Integer[], true, [10,20,30,40,50,60,70,80,90].to_java(:Integer)].to_java,
    ]
  )

  it 'returns HashMap<Integer,Double> values' do
    expect(f.getValueType).to eql(java.util.HashMap.java_class)
  end

  it 'returns 9 values as percentiles, and reflect for leave()' do
    (0...100).each do |i|
      f._call(:enter, [i.to_s, [10,20,30,40,50,60,70,80,90].to_java(:Integer)].to_java)
    end

    r = f.getValue
    expect(r.size).to eql(9)
    expect(r["10"]).to eql(10.0)
    expect(r["20"]).to eql(20.0)
    expect(r["30"]).to eql(30.0)
    expect(r["40"]).to eql(40.0)
    expect(r["50"]).to eql(50.0)
    expect(r["60"]).to eql(60.0)
    expect(r["70"]).to eql(70.0)
    expect(r["80"]).to eql(80.0)
    expect(r["90"]).to eql(90.0)

    (0...50).each do |i|
      f._call(:leave, [i.to_s, [10,20,30,40,50,60,70,80,90].to_java(:Integer)].to_java)
    end

    expect(f.instance_eval{ @func.valueListSize }).to eql(50)

    r = f.getValue
    expect(r.size).to eql(9)
    expect(r["10"]).to eql(55.0)
    expect(r["20"]).to eql(60.0)
    expect(r["30"]).to eql(65.0)
    expect(r["40"]).to eql(70.0)
    expect(r["50"]).to eql(75.0)
    expect(r["60"]).to eql(80.0)
    expect(r["70"]).to eql(85.0)
    expect(r["80"]).to eql(90.0)
    expect(r["90"]).to eql(95.0)
  end

  f2 = udf_function(
    Norikra::UDF::Percentiles,
    :valueType => java.util.HashMap,
    :parameters => [
      [java.lang.Integer, false],
      [java.lang.Integer[], true, [50,90,95,98,99].to_java(:Integer)].to_java,
    ]
  )
  it 'returns 5 values as percentiles, and returns same value after all values leaving' do
    (0...2000).each do |i|
      f2._call(:enter, [i, [50,90,95,98,99].to_java(:Integer), 100].to_java)
    end

    expect(f2.instance_eval{ @func.valueListSize }).to eql(2000)

    r = f2.getValue
    expect(r.size).to eql(5)

    (0...2000).to_a.shuffle.each do |i|
      f2._call(:leave, [i, [50,90,95,98,99].to_java(:Integer), 100].to_java)
    end

    r2 = f2.getValue
    expect(r2.size).to eql(r.size)
    r2.keys.each do |k|
      expect(r2[k]).to eql(r[k])
    end
  end
end
