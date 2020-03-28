package com.mmo.hackernews.`object`

import java.io.IOException
import java.io.ObjectStreamException
import java.io.Serializable

class Article(var objectID: String, var title: String, var author: String, var url: String, var createdAtTimestamp: Long, var swipedOut : String) : Serializable {

    companion object {
        private const val serialVersionUID = 1L
    }

    override fun toString(): String {
        return "Article(objectID='$objectID', title='$title', author='$author', url='$url', createdAtTimestamp=$createdAtTimestamp, swipedOut='$swipedOut')"
    }

    @Throws(IOException :: class)
    private fun writeObject(out : java.io.ObjectOutputStream){
        out.writeUTF(objectID)
        out.writeUTF(title)
        out.writeUTF(author)
        out.writeUTF(url)
        out.writeUTF(createdAtTimestamp.toString())
        out.writeUTF(swipedOut)
    }
    @Throws(IOException :: class, ClassNotFoundException::class)
    private fun readObject(inStream: java.io.ObjectInputStream){
        objectID = inStream.readUTF()
        title = inStream.readUTF()
        author = inStream.readUTF()
        url = inStream.readUTF()
        createdAtTimestamp = inStream.readUTF().toLong()
        swipedOut = inStream.readUTF()
    }

    @Throws(ObjectStreamException::class)
    private fun readObjectNoData() {

    }

}