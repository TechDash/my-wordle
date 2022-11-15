package com.example.wordlepromax;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.TreeSet;

public class DictionaryBST implements Parcelable {
  private TreeSet<String> dict;

  public DictionaryBST() {
    dict = new TreeSet<String>();
  }


  /** Add this word to the dictionary.  Convert it to lowercase first
   * for the assignment requirements.
   * @param word The word to add
   * @return true if the word was added to the dictionary
   * (it wasn't already there). */
  public boolean addWord(String word) {
    boolean inDict = dict.contains(word);
    if (!inDict) {
      dict.add(word.toLowerCase());
      return true;
    }
    return false;
  }


  /** Return the number of words in the dictionary */
  public int size() {
    return dict.size();
  }

  /** Is this a word according to this dictionary? */
  public boolean isWord(String s) {
    return dict.contains(s.toLowerCase());
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeSerializable(this.dict);
  }

  public void readFromParcel(Parcel source) {
    this.dict = (TreeSet<String>) source.readSerializable();
  }

  protected DictionaryBST(Parcel in) {
    this.dict = (TreeSet<String>) in.readSerializable();
  }

  public static final Parcelable.Creator<DictionaryBST> CREATOR = new Parcelable.Creator<DictionaryBST>() {
    @Override
    public DictionaryBST createFromParcel(Parcel source) {
      return new DictionaryBST(source);
    }

    @Override
    public DictionaryBST[] newArray(int size) {
      return new DictionaryBST[size];
    }
  };
}
