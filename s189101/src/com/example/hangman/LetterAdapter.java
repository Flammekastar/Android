package com.example.hangman;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.widget.Button;

public class LetterAdapter extends BaseAdapter {

	private String[] letters;
	private LayoutInflater letterInf;
	
	public LetterAdapter(Context c) {
		letters=new String[26];
		for (int a = 0; a < letters.length; a++) {
		  letters[a] = "" + (char)(a+'A');
		}
		letterInf = LayoutInflater.from(c);
		}
	
	@Override
	public int getCount() {
		  return letters.length;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
	  Button letterBtn;
	  if (convertView == null) {
	    letterBtn = (Button)letterInf.inflate(R.layout.letter, parent, false);
	  } else {
	    letterBtn = (Button) convertView;
	  }
	  letterBtn.setText(letters[position]);
	  return letterBtn;
	}

}
