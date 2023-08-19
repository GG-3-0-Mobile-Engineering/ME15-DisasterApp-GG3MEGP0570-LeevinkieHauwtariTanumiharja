package com.gigih.disastermap

import com.gigih.disastermap.domain.DisasterDomain
import org.junit.Assert.assertEquals
import org.junit.Test

class DisasterDomainTest {

    @Test
    fun testParseDate() {
        val inputDateString = "2023-07-27T00:52:21.834Z"
        val expectedOutput = "27 Jul 2023, 00:52"

        val result = DisasterDomain.parseDate(inputDateString)
        assertEquals(expectedOutput, result)
    }
}