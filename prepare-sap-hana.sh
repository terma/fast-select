#!/usr/bin/env bash

# setup docker container
docker run -it --name sap-hana -v /Users/terma/Downloads/sap-hana:/sap-hana-install opensuse bash
# connect to existent container

# in container
cd /sap-hana-install
zypper update
zypper install numactl
./setup-


# ...
# yum install libtool
# yum install libtool-ltdl
# to get centos install GLIBCXX_3.4.20
# yum install centos-release-scl-rh
# yum install devtoolset-3-gcc devtoolset-3-gcc-c++
# scl enable devtoolset-3 bash
