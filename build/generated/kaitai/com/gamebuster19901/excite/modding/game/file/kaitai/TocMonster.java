// This is a generated file! Please edit source .ksy file and use kaitai-struct-compiler to rebuild

package com.gamebuster19901.excite.modding.game.file.kaitai;

import io.kaitai.struct.ByteBufferKaitaiStream;
import io.kaitai.struct.KaitaiStruct;
import io.kaitai.struct.KaitaiStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.nio.charset.Charset;

public class TocMonster extends KaitaiStruct {
    public static TocMonster fromFile(String fileName) throws IOException {
        return new TocMonster(new ByteBufferKaitaiStream(fileName));
    }

    public TocMonster(KaitaiStream _io) {
        this(_io, null, null);
    }

    public TocMonster(KaitaiStream _io, KaitaiStruct _parent) {
        this(_io, _parent, null);
    }

    public TocMonster(KaitaiStream _io, KaitaiStruct _parent, TocMonster _root) {
        super(_io);
        this._parent = _parent;
        this._root = _root == null ? this : _root;
        _read();
    }
    private void _read() {
        this.header = new Header(this._io, this, _root);
        this.details = new ArrayList<Details>();
        for (int i = 0; i < header().numFile(); i++) {
            this.details.add(new Details(this._io, this, _root));
        }
        this.filename = new ArrayList<Filename>();
        for (int i = 0; i < header().numFile(); i++) {
            this.filename.add(new Filename(this._io, this, _root));
        }
        this.padding = new Padding(this._io, this, _root);
    }
    public static class Header extends KaitaiStruct {
        public static Header fromFile(String fileName) throws IOException {
            return new Header(new ByteBufferKaitaiStream(fileName));
        }

        public Header(KaitaiStream _io) {
            this(_io, null, null);
        }

        public Header(KaitaiStream _io, TocMonster _parent) {
            this(_io, _parent, null);
        }

        public Header(KaitaiStream _io, TocMonster _parent, TocMonster _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.magic1 = this._io.readBytes(8);
            if (!(Arrays.equals(magic1(), new byte[] { 48, 83, 69, 82, 67, 79, 84, 69 }))) {
                throw new KaitaiStream.ValidationNotEqualError(new byte[] { 48, 83, 69, 82, 67, 79, 84, 69 }, magic1(), _io(), "/types/header/seq/0");
            }
            this.version1 = this._io.readU4le();
            this.unknown1 = this._io.readU4le();
            this.magic2 = this._io.readBytes(4);
            if (!(Arrays.equals(magic2(), new byte[] { 33, 73, 71, 77 }))) {
                throw new KaitaiStream.ValidationNotEqualError(new byte[] { 33, 73, 71, 77 }, magic2(), _io(), "/types/header/seq/3");
            }
            this.version2 = this._io.readU4le();
            this.unknown2 = this._io.readU4le();
            this.unixTime = this._io.readU4le();
            this.numFile = this._io.readU4le();
            this.uncompressedResSize = this._io.readU4le();
            this.compressedResSize = this._io.readU4le();
            this.unknown3 = this._io.readU4le();
            this.filenameDirLen = this._io.readU4le();
        }
        private byte[] magic1;
        private long version1;
        private long unknown1;
        private byte[] magic2;
        private long version2;
        private long unknown2;
        private long unixTime;
        private long numFile;
        private long uncompressedResSize;
        private long compressedResSize;
        private long unknown3;
        private long filenameDirLen;
        private TocMonster _root;
        private TocMonster _parent;
        public byte[] magic1() { return magic1; }
        public long version1() { return version1; }
        public long unknown1() { return unknown1; }
        public byte[] magic2() { return magic2; }
        public long version2() { return version2; }
        public long unknown2() { return unknown2; }

        /**
         * Creation Date
         */
        public long unixTime() { return unixTime; }
        public long numFile() { return numFile; }

        /**
         * Add 128
         */
        public long uncompressedResSize() { return uncompressedResSize; }

        /**
         * Add 128
         */
        public long compressedResSize() { return compressedResSize; }

        /**
         * Either 0 128 or 1152
         */
        public long unknown3() { return unknown3; }
        public long filenameDirLen() { return filenameDirLen; }
        public TocMonster _root() { return _root; }
        public TocMonster _parent() { return _parent; }
    }
    public static class Details extends KaitaiStruct {
        public static Details fromFile(String fileName) throws IOException {
            return new Details(new ByteBufferKaitaiStream(fileName));
        }

        public Details(KaitaiStream _io) {
            this(_io, null, null);
        }

        public Details(KaitaiStream _io, TocMonster _parent) {
            this(_io, _parent, null);
        }

        public Details(KaitaiStream _io, TocMonster _parent, TocMonster _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.offsetFilename = this._io.readU4le();
            this.typeCode = new String(this._io.readBytes(4), Charset.forName("utf-8"));
            this.typeCodeInt = this._io.readU4le();
            this.fileSize = this._io.readU4le();
            this.fileOffset = this._io.readU4le();
            this.hash = this._io.readU4le();
            this.unknown1 = new ArrayList<Integer>();
            for (int i = 0; i < 16; i++) {
                this.unknown1.add(this._io.readU1());
            }
        }
        private long offsetFilename;
        private String typeCode;
        private long typeCodeInt;
        private long fileSize;
        private long fileOffset;
        private long hash;
        private ArrayList<Integer> unknown1;
        private TocMonster _root;
        private TocMonster _parent;
        public long offsetFilename() { return offsetFilename; }
        public String typeCode() { return typeCode; }
        public long typeCodeInt() { return typeCodeInt; }
        public long fileSize() { return fileSize; }

        /**
         * Add 128
         */
        public long fileOffset() { return fileOffset; }
        public long hash() { return hash; }
        public ArrayList<Integer> unknown1() { return unknown1; }
        public TocMonster _root() { return _root; }
        public TocMonster _parent() { return _parent; }
    }
    public static class Filename extends KaitaiStruct {
        public static Filename fromFile(String fileName) throws IOException {
            return new Filename(new ByteBufferKaitaiStream(fileName));
        }

        public Filename(KaitaiStream _io) {
            this(_io, null, null);
        }

        public Filename(KaitaiStream _io, TocMonster _parent) {
            this(_io, _parent, null);
        }

        public Filename(KaitaiStream _io, TocMonster _parent, TocMonster _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.filename = new String(this._io.readBytesTerm((byte) 0, false, true, true), Charset.forName("utf-8"));
        }
        private String filename;
        private TocMonster _root;
        private TocMonster _parent;
        public String filename() { return filename; }
        public TocMonster _root() { return _root; }
        public TocMonster _parent() { return _parent; }
    }
    public static class Padding extends KaitaiStruct {
        public static Padding fromFile(String fileName) throws IOException {
            return new Padding(new ByteBufferKaitaiStream(fileName));
        }

        public Padding(KaitaiStream _io) {
            this(_io, null, null);
        }

        public Padding(KaitaiStream _io, TocMonster _parent) {
            this(_io, _parent, null);
        }

        public Padding(KaitaiStream _io, TocMonster _parent, TocMonster _root) {
            super(_io);
            this._parent = _parent;
            this._root = _root;
            _read();
        }
        private void _read() {
            this.padding = new ArrayList<Integer>();
            {
                int i = 0;
                while (!this._io.isEof()) {
                    this.padding.add(this._io.readU1());
                    i++;
                }
            }
        }
        private ArrayList<Integer> padding;
        private TocMonster _root;
        private TocMonster _parent;
        public ArrayList<Integer> padding() { return padding; }
        public TocMonster _root() { return _root; }
        public TocMonster _parent() { return _parent; }
    }
    private Header header;
    private ArrayList<Details> details;
    private ArrayList<Filename> filename;
    private Padding padding;
    private TocMonster _root;
    private KaitaiStruct _parent;
    public Header header() { return header; }
    public ArrayList<Details> details() { return details; }
    public ArrayList<Filename> filename() { return filename; }
    public Padding padding() { return padding; }
    public TocMonster _root() { return _root; }
    public KaitaiStruct _parent() { return _parent; }
}
