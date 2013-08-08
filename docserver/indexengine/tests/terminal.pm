#!/usr/bin/perl -w

#package terminalcolor;

my %forecolor_const = (
		"black"     => 30,
		"red"       => 31,
		"green"     => 32,
		"orange"    => 33,
		"blue"      => 34,
		"magenta"   => 35,
		"cyan"      => 36,
		"white"     => 37
		);

my %backcolor_const = (
		"black"     => 40,
		"red"       => 41,
		"green"     => 42,
		"orange"    => 43,
		"blue"      => 44,
		"magenta"   => 45,
		"cyan"      => 46,
		"white"     => 47,
		"none"    => 49
		);

my %action_const = (
		"bold"      => 1,
		"hidden"    => 2,
		"underline" => 4,
		"blink"     => 5,
		"reverse"   => 7
		);

# set the color according to some special rules
# we should use this functioin like:
# setcolor($string, $forecolor, $backcolor, $action);
# it will return the modified string
sub setcolor {
	my $string=shift;
	my $forecolor=shift || "red";
	my $backcolor=shift || "none";
	my $action=shift || "bold";

	if (!exists $forecolor_const{$forecolor} ||
			!exists $backcolor_const{$backcolor} ||
			!exists $action_const{$action}) {
		return $string;
	}
	$string = "\033[$action_const{$action};$forecolor_const{$forecolor};$backcolor_const{$backcolor}m".$string."\033[0m";
	return $string;
}
