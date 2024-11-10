FILES:${PN} += " \
    ${bindir}/g++ \
	${bindir}/gcc \
	${bindir}/cpp \
    ${bindir}/gcov \
	${bindir}/gcov-tool \
    ${bindir}/c++ \
	${bindir}/cc \
"

FILES:${PN} += " \
	${libdir}/gcc/${TARGET_SYS}/${BINV}/include/c++ \
"

do_install:append () {
	ln -sf ${TARGET_PREFIX}g++-${BINV} g++
	ln -sf ${TARGET_PREFIX}gcc-${BINV} gcc
	ln -sf ${TARGET_PREFIX}cpp-${BINV} cpp
	ln -sf ${TARGET_PREFIX}gcov-${BINV} gcov
	ln -sf ${TARGET_PREFIX}gcov-tool-${BINV} gcov-tool
	ln -sf g++-${BINV} c++
	ln -sf gcc-${BINV} cc
	chown -R root:root ${D}
}

do_install:append() {
	ln -sf ${includedir}/c++/${BINV} ${D}${libdir}/gcc/${TARGET_SYS}/${BINV}/include/c++
}
