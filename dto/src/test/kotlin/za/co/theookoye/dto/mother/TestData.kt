package za.co.theookoye.dto.mother

import java.time.LocalDateTime
import java.util.*
import kotlin.math.absoluteValue

object TestData {
    
    fun uuid(): UUID = UUID.randomUUID()
    
    fun uuidStr() = uuid().toString()
    
    fun string() = (1..20)
        .map { uuidStr().replace("-", "").random() }
        .joinToString("")
    
    fun long() = System.nanoTime()
    
    fun int() = Random().nextInt().absoluteValue
    
    fun bool() = kotlin.random.Random.nextBoolean()
    
    fun <T> enum(values: Array<T>) = values[Random().nextInt(values.size)]
    
    fun bigAmount() = Random().nextInt(10000).absoluteValue.toBigDecimal()
    
    private fun currency(): Currency = Currency
        .getAvailableCurrencies()
        .elementAt(Random().nextInt(Currency.getAvailableCurrencies().size))
    
    fun currencyCode(): String = currency().currencyCode
    
    fun now(): LocalDateTime = LocalDateTime.now()
    
    fun past(): LocalDateTime = LocalDateTime.now().minusDays(Random().nextInt(365).toLong())
    
    fun future(): LocalDateTime = LocalDateTime.now().plusDays(Random().nextInt(365).toLong())
}