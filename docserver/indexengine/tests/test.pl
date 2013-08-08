#!/usr/bin/perl -w

use strict;
use terminal;

my $flag = 0;
my $testName;
my $total = "total";
my @content = `./test`;
my $fail = "FAILED";
my $pass = "PASS";
my $res;

if ($#ARGV < 0) {
	print "参数不正确,正确的是: perl test.pl TestName\n";
	exit(1);
} else {
	$testName = $ARGV[0];
}

foreach(@content)
{
	if($_ =~ /$testName/)
	{
		$flag = 1;
	}
	if($flag == 1)
	{
		$res = setcolor($_, "green", "none");
		print $res;
	}
	if($_ =~ /$total/)
	{
		$flag = 0;
	}
	if($_ =~ /$fail/ )
	{
		$res = setcolor($_, "red", "none", "bold");
		print $res;
	}
	if($_ =~ /$pass/ )
	{
		$res = setcolor($_, "blue", "none", "bold");
		print $res;
	}
}
