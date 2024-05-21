package org.devkokov.from_into

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertIs

class FromIntoTest {
    @Test
    fun successfullyTransformTypesUsingFromAndInto() {
        assertIs<A>(A.from(B()))
        assertIs<A>(A.from(C()))
        assertIs<B>(B.from(A()))

        assertIs<A>(C().into(A::class))
        assertIs<B>(A().into(B::class))

        assertIs<A>(C().into<C, A>())
        assertIs<B>(A().into<A, B>())

        assertIs<A>(takesA(C().into()))
        assertIs<B>(takesB(A().into()))
    }

    @Test
    fun throwsExceptionWhenFromIsNotImplemented() {
        val exception = assertFailsWith<FromIntoException>(
            block = { takesC(A().into()) }
        )
        assertEquals("A cannot be converted into C. Does C implement `from(obj: A): C` ?", exception.message)
    }

    private fun takesA(v: A): A = v
    private fun takesB(v: B): B = v
    private fun takesC(v: C): C = v
}

class A {
    companion object {
        fun from(obj: B): A = obj.run { A() }
        fun from(obj: C): A = obj.run { A() }
    }
}

class B {
    companion object {
        fun from(obj: A): B = obj.run { B() }
    }
}

class C
