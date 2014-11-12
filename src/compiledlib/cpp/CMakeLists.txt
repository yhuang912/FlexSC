project(oblivm)
cmake_minimum_required(VERSION 2.6.0 FATAL_ERROR)

find_package(Boost COMPONENTS timer system filesystem)

set(CMAKE_CXX_FLAGS "-O3 --std=c++0x -DNDEBUG -Wall")
#set(CMAKE_CXX_FLAGS "-g -ggdb --std=c++0x -Wall")

include_directories(${CMAKE_SOURCE_DIR})

add_executable(ssp
   dijkstra.cpp
)

add_executable(prim
   prim.cpp
)

add_executable(dfs
   dfs.cpp
)
