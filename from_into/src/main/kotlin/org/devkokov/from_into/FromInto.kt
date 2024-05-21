package org.devkokov.from_into

import kotlin.reflect.KClass
import kotlin.reflect.full.companionObject
import kotlin.reflect.full.companionObjectInstance
import kotlin.reflect.full.functions
import kotlin.reflect.jvm.jvmErasure

fun transform(from: KClass<*>, into: KClass<*>, obj: Any): Any? {
    return into.companionObject?.functions?.firstOrNull {
        it.name == "from"
                && it.parameters.count() == 2
                && it.parameters[0].type.jvmErasure == into.companionObject
                && it.parameters[1].type.jvmErasure == from
                && it.returnType.jvmErasure == into
    }?.call(into.companionObjectInstance, obj)
}

inline fun <reified T1 : Any, reified T2 : Any> T1.into(): T2 {
    return transform(T1::class, T2::class, this)?.let { it as T2 }
        ?: throw FromIntoException(T1::class, T2::class)
}

inline fun <reified T1 : Any, reified T2 : Any> T1.into(into: KClass<T2>): T2 {
    return transform(T1::class, into, this)?.let { it as T2 }
        ?: throw FromIntoException(T1::class, into)
}

class FromIntoException(from: KClass<*>, into: KClass<*>) :
    Exception("${from.simpleName} cannot be converted into ${into.simpleName}. Does ${into.simpleName} implement `from(obj: ${from.simpleName}): ${into.simpleName}` ?")
