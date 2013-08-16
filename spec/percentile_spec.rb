require 'norikra/udf_spec_helper'

include Norikra::UDFSpecHelper

require 'norikra/udf/percentile'

describe Norikra::UDF::Percentile do
  f = udf_function(
    Norikra::UDF::Percentile,
    :valueType => java.lang.Double[],
    :parameters => [
      [java.lang.String, false],
      [java.lang.Integer[], true, [10,20,30,40,50,60,70,80,90].to_java(:Integer)].to_java,
    ]
  )

  it 'returns Double[] values' do
    expect(f.getValueType).to eql(java.lang.Double[].java_class)
  end

  it 'returns 9 values as percentiles' do
    (0...100).each do |i|
      f._call(:enter, [i.to_s, [10,20,30,40,50,60,70,80,90].to_java(:Integer)].to_java)
    end

    r = f.getValue
    expect(r.size).to eql(9)
    expect(r[0]).to eql(10.0)
    expect(r[1]).to eql(20.0)
    expect(r[2]).to eql(30.0)
    expect(r[3]).to eql(40.0)
    expect(r[4]).to eql(50.0)
    expect(r[5]).to eql(60.0)
    expect(r[6]).to eql(70.0)
    expect(r[7]).to eql(80.0)
    expect(r[8]).to eql(90.0)
  end

  f2 = udf_function(
    Norikra::UDF::Percentile,
    :valueType => java.lang.Double[],
    :parameters => [
      [java.lang.Integer, false],
      [java.lang.Integer[], true, [50,90,95,98,99].to_java(:Integer)].to_java,
      [java.lang.Integer, true, 100]
    ]
  )
  it 'returns 5 values as percentiles' do
    (0..2000).to_a.shuffle.each do |i|
      f2._call(:enter, [i, [50,90,95,98,99].to_java(:Integer), 100].to_java)
    end

    expect(f2.instance_eval{ @func.sampleSize }).to eql(100)
    expect(f2.instance_eval{ @func.sampleNum }).to eql(51) # reSampleInterval * N + 1

    r = f2.getValue
    expect(r.size).to eql(5)
  end
end
