# coding: utf-8

Gem::Specification.new do |spec|
  spec.name          = "norikra-udf-percentile"
  spec.version       = "0.0.1"
  spec.authors       = ["TAGOMORI Satoshi"]
  spec.email         = ["tagomoris@gmail.com"]
  spec.description   = %q{TODO: Write a gem description}
  spec.summary       = %q{TODO: Write a gem summary}
  spec.homepage      = "https://github.com/tagomoris/norikra-udf-percentile"
  spec.license       = "GPLv2"
  spec.platform      = "java"

  spec.files         = `git ls-files`.split($/)
  spec.executables   = spec.files.grep(%r{^bin/}) { |f| File.basename(f) }
  spec.test_files    = spec.files.grep(%r{^(test|spec|features)/})
  spec.require_paths = ["lib", "jar"]

  spec.add_runtime_dependency "norikra"

  spec.add_development_dependency "bundler", "~> 1.3"
  spec.add_development_dependency "rake"
  spec.add_development_dependency "rspec"
end
