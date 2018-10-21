# Nokia Asha Offline Dictionary

An offline English dictionary app written for __Low RAM__ devices. This can be slightly modified to run on some smart watches too.

![](/docs/demo.gif)

## Working

The App uses `Skipped Partition Mapping` to look for a word and get its meaning. Dont worry, the term is unnecessarily complicated, what it does is fairly simple as explained below. 

First we got the entire dictonary from [Project Gutenberg - Webster's Dictonary](https://www.gutenberg.org/).

The total size of the unzipped file is **~28MB**. Looks small, but this is humongous for a lot of memory constraint devices like low-end smart watches, arduino/other prototyping boards, my Nokia Asha etc. So we divide it into raw data stream chunks of 1MB or even 100KB each. The device can now process this much size file.

However, to look whether the words even exists in the dictonary we have to sift through all these chunks!! This defeats the purpose of chunkning it. So we create another file that only contains a list of all the available words and where their meaning is located  _(in which chunk's which location)_.

Cool, now we created a map to actual data. But, turns out the map itself is **~3MB** in size :|. Going through this is still gonna take time. So, we divide this map in 26 parts - one for each alphabet. In this way when the user searches for a word starting with _C_, we only look into _C's_ map which is **~270KB**.

In theory, 270KB sounds doable. Alas, it turns out, in real time even this causes a very visible lag. So, buffer only **1KB** of data at time and do a `median search` to check if the word might be present in the buffer, else take the median of _total search area_ of buffer at jump to that location in 270KB. Keep doing this and you will ultimately get your word. 

## Build

1. Clone or Download the repo.
2. Download Nokia Asha SDK.
3. Load the project and build.

## Run

1. Either build as decribed above or download a release.
2. Copy to emulator or device.
3. Click the Dictionary icon from App Drawer.
