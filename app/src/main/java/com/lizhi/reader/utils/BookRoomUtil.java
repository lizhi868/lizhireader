package com.lizhi.reader.utils;


import java.util.ArrayList;

public class BookRoomUtil
{
	private BookRoomUtil()
	{

	}

	public static class BookSource
	{
		public String strName; // 书名
		public String strAuthor; // 作者
		public String strDesc; // 描述
		public String strImage; // 封面url
		public boolean bFinish; // 是否完结
		public int nHot; // 热度 1--5
		public String strSummary; //男生女生
		public String strMainType; // 奇幻、玄幻
		public String strSubType; // 东方玄幻, 西方玄幻
	}

	public static BookRoomUtil bookroom = new BookRoomUtil();
	public static BookRoomUtil GetInstance()
	{
		return bookroom;
	}

	public ArrayList<BookSource> querySource(String summary, String maintype, int anchor, int count)
	{
		ArrayList<BookSource> list = new ArrayList<>();

		BookSource bookSource = new BookSource();
		bookSource.strName = "黑帮";
		bookSource.strAuthor = "黑帮";
		bookSource.strDesc = "黑帮";
		bookSource.strImage = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1586792017040&di=508d7af95fafeb5515ddc7633ebe45dc&imgtype=0&src=http%3A%2F%2Fpic.90sjimg.com%2Fdesign%2F00%2F57%2F93%2F24%2F58facdf0a69a0.png";
		bookSource.bFinish = true;
		bookSource.nHot = 5;
		bookSource.strSummary = summary;
		bookSource.strMainType = maintype;
		bookSource.strSubType = "东方玄幻";

		list.add(bookSource);
		return list;
	}

}
