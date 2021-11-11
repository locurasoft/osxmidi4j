[![](https://jitpack.io/v/umjammer/osxmidi4j.svg)](https://jitpack.io/#umjammer/osxmidi4j)
 [![Java CI with Maven](https://github.com/umjammer/osxmidi4j/actions/workflows/maven.yml/badge.svg)](https://github.com/umjammer/osxmidi4j/actions/workflows/maven.yml)

osxmidi4j
=========

osxmidi4j provides the Java MIDI API including Sysex messages for the Mac OS X platform.

## Release Notes

### Version 1.0

* Works with Mac OS X 10.8.5

## Usage

### Maven

 * [jitpack](https://jitpack.io/#umjammer/osxmidi4j)

## Project info

As long as the CoreMidi API and the Java MIDI API remain the same the library will probably work as expected. Therefore there is currently no planned roadmap. Instead a dot version update will be released every time the library has been verified on a new version of Mac OS X.

Don't hesitate to submit issues if you find bugs or come up with improvment suggestions.

## Technical description

osxmidi4j will register itself automatically to the Java runtime as a MIDI device provider.
Call the standard Java MIDI API
All osxmidi4j devices will be prefixed with "CoreMidi - "

This library works in two ways
* Bundled inside an application 
* A Java extension residing on a single machine.

### CoreMidi services API

The interface net.sourceforge.osxmidi4j.midiservices.CoreMidiLibrary provides the Mac OS X CoreMidi services API for Java.
It is (partly) generated by JNAerator and relies heavily on the Rococoa library and JNA.
