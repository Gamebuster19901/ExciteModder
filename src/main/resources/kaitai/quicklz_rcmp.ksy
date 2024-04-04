meta:
  id: quicklz_rcmp
  title: quickLZ compressed data
  endian: le
seq:
  - id: header
    type: header
  - id: bytes
    size-eos: true
    process: com.gamebuster19901.excite.modding.quicklz.quicklz_dumper
types:
  header:
    seq:
      - id: magic
        contents: "PMCr"
        size: 4
      - id: unknown
        type: u4
      - id: compressed_length
        type: u4
      - id: uncompressed_length
        type: u4
