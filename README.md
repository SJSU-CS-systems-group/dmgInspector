

# DMGInspector CLI

DMGInspector is a Java-based command line interface for inspecting data structures and extracting files from a DMG file’s underlying APFS image. 

---
## I. Key Features



1. View volume info
2. View file system object info
3. Extract a specific file
4. Extract all files
---

## II. Usage

```
dmgi [-hsV] [--extractAll] [--volumes] [--extractOne=<fileId>] -p=<path>
```


<table>
  <tr>
   <td>

<strong>OPTION</strong>
   </td>
   <td><strong>DESCRIPTION</strong>
   </td>
  </tr>
  <tr>
   <td><code>-h, --help</code>
   </td>
   <td>Show this help message and exit
   </td>
  </tr>
  <tr>
   <td><code>-p, --path </code>
   </td>
   <td>Path of the dmg file
   </td>
  </tr>
  <tr>
   <td><code>-s, -show</code>
   </td>
   <td>Show all files
   </td>
  </tr>
  <tr>
   <td><code>--extractAll</code>
   </td>
   <td>Extract all the content of the file
   </td>
  </tr>
  <tr>
   <td><code>--extractOne=&lt;fileId></code>
   </td>
   <td>Extract one file
   </td>
  </tr>
  <tr>
   <td><code>--volumes</code>
   </td>
   <td>Print all the volumes in the APFS Structure
   </td>
  </tr>
</table>

---

## III. How It Works


### A. DMG File Parsing

#### How it works



1. Read an inputted DMG file
2. Parse plist XML by looking for the “koly” trailer
3. Parse “mish”blocks from PList XML
4. Parse “blkx” block data from mish block base64 data
5. Decompress compressed data fork bytes specified by blkx block offset & length values
6. Save decompressed bytes to files. File names are based on their name in the plist (e.g. `diskimageApple_APFS4`)

#### Implementation


<table>
  <tr>
   <td><strong>CLASS</strong>
   </td>
   <td><strong>DESCRIPTION</strong>
   </td>
  </tr>
  <tr>
   <td>DMGInspector
   </td>
   <td>The central class for parsing a DMG file at a specified file path. It parses and stores instances of the DMG’s…
<ol>

<li>KOLY block 

<li>plist

<p>
This class parses the KOLY block first to obtain offsets & lengths of…
<ol>

<li>the DMG’s data fork bytes

<li>The DMG’s plist bytes

<p>
These are used to parse the DMG’s Plist.
</li>
</ol>
</li>
</ol>
   </td>
  </tr>
  <tr>
   <td>DMGKolyBlock
   </td>
   <td>This class parses a DMG’s KOLY block, which is a trailer containing data such as…
<ol>

<li>data fork offsets and lengths

<li>plist offsets and lengths
</li>
</ol>
   </td>
  </tr>
  <tr>
   <td>Plist
   </td>
   <td>This class extracts a DMG’s partitions listed by its plist XML structure. These partitions are obtained through the following steps…
<ol>

<li>Parse the plist XML from a ByteBuffer of plist bytes

<li>Get the partition’s name from text content corresponding to the &lt;CFName> plist key

<li>Base64 decode the text content corresponding to the &lt;Data> plist key

<li>Parse “mish” blocks from the resulting bytes

<li>Parse “blkx” chunks specified by each mish block

<li>Decompress the bytes corresponding to the compressed offset & length specified by each blkx chunk. These offsets & lengths are applied relative to the DMG’s data fork bytes.

<li>Save the partition files, naming them after their CFName and with their decompressed blkx chunk data.
</li>
</ol>
   </td>
  </tr>
  <tr>
   <td>MishBlock
   </td>
   <td>Parses a “mish” block structure from provided plist &lt;Data> bytes. Alongside many other fields, it contains a list of “blkx” chunks.
   </td>
  </tr>
  <tr>
   <td>MishBlock.BLKXChunkEntry
   </td>
   <td>Nested class that parses “blkx” chunks, which provide compressed byte offsets and lengths.
   </td>
  </tr>
</table>



### B. APFS Image Parsing

#### How it works
1. Read the APFS image file outputted by the DMG parser
2. Parse the Container Superblock (CSB)
3. Parse Volumes associated with the CSB’s field of Volume Object Identifiers
4. Starting from a volume’s root “DREC” directory record, parse all child directory and file info from their respective DREC, EXTENT, and INODE records.

#### Implementation
<table>
  <tr>
   <td><strong>CLASS</strong>
   </td>
   <td><strong>DESCRIPTION</strong>
   </td>
  </tr>
  <tr>
   <td>APFS
   </td>
   <td>The central class for parsing APFS image files. It parses the APFS image at the specified path, tracking instances of…
<ol>

<li>The Container Superblock

<li>Volumes
</li>
</ol>
   </td>
  </tr>
  <tr>
   <td>BlockHeader
   </td>
   <td>All APFS object have a BlockHeader -- e.g. OMap, APFSContainer, APFSVolume…
<p>
These block headers contain fields such as checksum, block ID, and flags.
   </td>
  </tr>
  <tr>
   <td>OMap
   </td>
   <td>APFS OMap structure found using physical offsets specified by the Container and Volume superblocks. Containers and Volumes have their own OMap, which map OIDs to physical addresses. 
<p>
This implementation also parses volume file system object B-trees into an OMap, so the APFSVolume class can quickly find records such as DRECs, Inodes, and Extents.
   </td>
  </tr>
  <tr>
   <td>APFSContainer
   </td>
   <td>Parses an APFS image’s container superblock. It tracks info such as the container omap OMap and volume oids.
<p>
An APFS file only has one container superblock. It is the first block of the APFS image file.
   </td>
  </tr>
  <tr>
   <td>APFSVolume
   </td>
   <td>Parses APFS image volume blocks. Has methods for printing/extracting files within the volume.
<p>
Internally, this class parses the volume file system object B-Tree. With DREC, Inode, and Extent data accessible through the resulting OMap, it parses the volume’s files using breadth-first search style traversal starting from the root DREC. 
   </td>
  </tr>
  <tr>
   <td>Keys & Values
   </td>
   <td>These classes parse…
<ol>

<li>Fixed-length OMap keys and values

<li>Variable-length FS object keys and values

<p>
The APFSVolume class uses these structures to parse keys and values based on a volume’s table of contents. APFSVolume has a fixed key-value flag that determines whether it needs to parse OMap keys or variable-length FS object keys.
</li>
</ol>
   </td>
  </tr>
</table>


---
## IV. Reference
1. DMG Format: [DeMystifyinG the DMG file format (newosxbook.com)](http://newosxbook.com/DMG.html)
2. Official APFS Reference: [Apple File System Reference](https://developer.apple.com/support/downloads/Apple-File-System-Reference.pdf)
3. APFS Cheat Sheet: [FOR518_APFS_CheatSheet_012020.pdf (contentstack.io)](https://assets.contentstack.io/v3/assets/blt36c2e63521272fdc/blt61c336e02577e733/5eb0940e248a28605479ccf0/FOR518_APFS_CheatSheet_012020.pdf)