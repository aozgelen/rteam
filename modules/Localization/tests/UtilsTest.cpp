/*
 * UtilsTest.cpp
 *
 *  Created on: Jun 17, 2010
 *      Author: appleapple
 */

#include <gtest/gtest.h>
#include "../src/Utils.h"

TEST(UtilsTest, GaussianDistributionTest)
{

	double g = Utils::gaussian(0, 0, 1);

	ASSERT_LT(0.398, g);
	ASSERT_GT(0.399, g);

	g = Utils::gaussian(0, 0, .01);

	ASSERT_LT(3.98, g);
	ASSERT_GT(3.99, g);

	g = Utils::gaussian(1, 0, 1);

	ASSERT_LT(.24, g);
	ASSERT_GT(.25, g);
}
