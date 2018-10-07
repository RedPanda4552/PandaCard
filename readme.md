# PandaCard

## About PandaCard
A pure Java, GUI-based tool for manipulating PS2 memory card images for emulators. The goal of this project in the long term is to provide a multiplatform, easy to use, one-stop-shop for memory card manipulation. Currently the main use is to merge AR Max format saves into a PCSX2 folder-based memory card, however tenative plans are in place to support more formats down the line.

## Compatibility
PandaCard can currently merge PS2 Save files into the following memory card types:
* PCSX2 folder-type

PandaCard can currently merge the following PS2 save file formats into a memory card:
* Action Replay Max Drive

Tentative plans are in place to extend support to the following at some point in the future, in no particular order:
* PCSX2 8 MB file-type memory card
* Dobiestation memory card (card structure currently unknown; Dobiestation has not implemented these yet)
* CodeBreaker save format
* X-Port save format
* PSV (Playstation 3 Virtual Memory Card) save format

## System Requirements
* Java SE, version 8 or newer.

## How to Launch
On Windows with Java installed, the PandaCard jar can be double clicked and launched. This behavior has not been tested on a Unix-based system. If this behavior fails, PandaCard can be launched from a command shell with `java -jar PandaCard-x.y.z.jar`

## Download
[Downloads are available here on the Releases tab](https://github.com/RedPanda4552/PandaCard/releases).
