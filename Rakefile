require "bundler/gem_tasks"

require 'rspec/core/rake_task'
RSpec::Core::RakeTask.new(:spec) do |t|
  t.rspec_opts = ["-c", "-f progress"] # '--format specdoc'
  t.pattern = 'spec/**/*_spec.rb'
end

task :compile do
  require 'rubygems'

  jarname = FileList['norikra-udf-*.gemspec'].first.gsub(/\.gemspec$/, '.jar')

  jarfiles = FileList['jar/**/*.jar'].select{|f| not f.end_with?('/' + jarname)}
  jarfiles << Gem.find_files('esper-*.jar').first
  classpath = "-classpath java:#{jarfiles.join(':')}"

  FileList['java/**/*.java'].each do |fn|
    sh "env LC_ALL=C javac #{classpath} #{fn}"
  end
  sh "env LC_ALL=C jar -cf jar/#{jarname} -C java ."
end

task :test => [:compile, :spec]
task :default => :test

task :all => [:compile, :spec, :build]
