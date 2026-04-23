{pkgs ? import <nixpkgs> {}}: let
  lz4c = pkgs.runCommand "lz4c" {} ''
    mkdir -p $out/bin
    ln -s ${pkgs.lz4.out}/bin/lz4 $out/bin/lz4c
  '';
in
  pkgs.mkShellNoCC {
    packages = with pkgs; [
      bc
      kmod
      bash
      gnused
      gnugrep
      systemd
      openssl
      libusb1
      binutils
      coreutils
      util-linux
      unixtools.xxd
      fuse-overlayfs
      (python312.withPackages (p: [p.pyyaml]))
      lz4
      lz4c
    ];
  }

