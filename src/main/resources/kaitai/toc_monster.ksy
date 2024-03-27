meta:
  id: toc_monster
  file-extension: toc
  title: Monster Games Table of Contents
  endian: le
seq:
  - id: header
    type: header
  - id: details
    type: details
    repeat: expr
    repeat-expr: header.num_file
  - id: filename
    type: filename
    repeat: expr
    repeat-expr: header.num_file
types:
  header:
    seq:
      - id: magic_1
        contents: "0SERCOTE"
        size: 8
      - id: version_1
        type: u4
      - id: unknown_1
        type: u4
      - id: magic_2
        contents: "!IGM"
        size: 4
      - id: version_2
        type: u4
      - id: unknown_2
        type: u4
      - id: unix_time
        type: u4
        doc: Creation Date
      - id: num_file
        type: u4
      - id: uncompressed_res_size
        type: u4
        doc: Add 128
      - id: compressed_res_size
        type: u4
        doc: Add 128
      - id: unknown_3
        type: u4
        doc: Either 0 128 or 1152
      - id: filename_dir_len
        type: u4
  details:
    seq:
      - id: offset_filename
        type: u4
      - id: type_code
        type: str
        encoding: utf-8
        size: 4
      - id: type_code_int
        type: u4
      - id: file_size
        type: u4
      - id: file_offset
        type: u4
        doc: Add 128
      - id: hash
        type: u4
      - id: unknown_1
        type: u1
        repeat: expr
        repeat-expr: 16
  filename:
    seq:
      - id: filename
        type: str
        encoding: utf-8
        terminator: 0