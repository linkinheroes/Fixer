package pl.janowicz.fixer

import junit.framework.Assert.assertEquals
import org.junit.Test
import pl.janowicz.fixer.api.DateAdapter
import java.util.*

class DateAdapterTest {

    @Test
    fun `date to string convertion test`() {
        val date = Calendar.getInstance().apply {
            set(2019, 6, 22)
        }
        assertEquals("2019-07-22", DateAdapter().toJson(date.time))
    }

    @Test
    fun `string to date convertion test`() {
        val dateText = "2019-07-22"
        val date = DateAdapter().fromJson(dateText)
        val calendar = Calendar.getInstance().apply {
            time = date
        }
        assertEquals(22, calendar.get(Calendar.DATE))
        assertEquals(6, calendar.get(Calendar.MONTH))
        assertEquals(2019, calendar.get(Calendar.YEAR))
    }
}