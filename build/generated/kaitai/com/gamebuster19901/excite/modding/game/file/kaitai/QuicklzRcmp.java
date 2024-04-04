// This is a generated file! Please edit source .ksy file and use kaitai-struct-compiler to rebuild

package com.gamebuster19901.excite.modding.game.file.kaitai;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.kaitai.struct.KaitaiStream;
import java.io.IOException;
import java.util.Arrays;

public class QuicklzRcmp extends KaitaiStruct {
    public static QuicklzRcmp fromFile(String fileName) throws IOException {
        return new QuicklzRcmp(new ByteBufferKaitaiStream(fileName));
    }

    public QuicklzRcmp(KaitaiStream _io) {
        this(_io, null, null);
    }

    public QuicklzRcmp(KaitaiStream _io, KaitaiStruct _parent) {
        this(_io, _parent, null);
    }

    public QuicklzRcmp(KaitaiStream _io, KaitaiStruct _parent, QuicklzRcmp _root) {
        super(_io);
        this._parent = _parent;
        this._root = _root == null ? this : _root;
        _read();
    }
    private void _read() {
        this.header = new Header(this._io, this, _root);
        this._raw_bytes = this._io.readBytesFull();
        com.gamebuster19901.excite.modding.quicklz.QuicklzDumper _process__raw_bytes = new com.gamebuster19901.excite.modding.quicklz.QuicklzDumper();
        this.bytes = _process__raw_bytes.decode(_raw_bytes);
    }
    public static class Header extends KaitaiStruct {
        public static Header fromFile(String fileName) throws IOException {
            return new Header(new ByteBufferKaitaiStream(fileName));
        }

        public Header(KaitaiStream _io) {
            this(_io, null, null);
        }

        public Header(KaitaiStream _io, QuicklzRcmp _parent) {
            this(_io, _parent, null);
        }

        public Header(KaitaiStream _io, QuicklzRcmp _parent, QuicklzRcmp _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.magic = this._io.readBytes(4);
            if (!(Arrays.equals(magic(), new byte[] { 80, 77, 67, 114 }))) {
                throw new KaitaiStream.ValidationNotEqualError(new byte[] { 80, 77, 67, 114 }, magic(), _io(), "/types/header/seq/0");
            }
            this.unknown = this._io.readU4le();
            this.compressedLength = this._io.readU4le();
            this.uncompressedLength = this._io.readU4le();
        }
        private byte[] magic;
        private long unknown;
        private long compressedLength;
        private long uncompressedLength;
        private QuicklzRcmp _root;
        private QuicklzRcmp _parent;
        public byte[] magic() { return magic; }
        public long unknown() { return unknown; }
        public long compressedLength() { return compressedLength; }
        public long uncompressedLength() { return uncompressedLength; }
        public QuicklzRcmp _root() { return _root; }
        public QuicklzRcmp _parent() { return _parent; }
    }
    private Header header;
    private byte[] bytes;
    private QuicklzRcmp _root;
    private KaitaiStruct _parent;
    private byte[] _raw_bytes;
    public Header header() { return header; }
    public byte[] bytes() { return bytes; }
    public QuicklzRcmp _root() { return _root; }
    public KaitaiStruct _parent() { return _parent; }
    public byte[] _raw_bytes() { return _raw_bytes; }
}
