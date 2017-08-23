/*
Copyright 2015-2017 Artem Stasiuk

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.github.terma.fastselect.benchmark;

interface Player {

    Object groupByWhereSimple() throws Exception;

    Object groupByWhereManySimple() throws Exception;

    Object groupByWhereIn() throws Exception;

    Object groupByWhereHugeIn() throws Exception;

    Object groupByWhereManyIn() throws Exception;

    Object groupByWhereManyHugeIn() throws Exception;

    Object groupByWhereRange() throws Exception;

    Object groupByWhereManyRange() throws Exception;

    Object groupByWhereStringLike() throws Exception;

    Object groupByWhereSpareStringLike() throws Exception;

    Object groupByWhereManyStringLike() throws Exception;

    Object groupByWhereString() throws Exception;

    Object groupByWhereManyString() throws Exception;

    Object groupByWhereSimpleRangeInStringLike() throws Exception;

    Object selectLimit() throws Exception;

    Object selectOrderByLimit() throws Exception;
}
