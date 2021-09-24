package com.hsbc.meets.dao.impl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import com.hsbc.meets.dao.MeetingRoomDao;
import com.hsbc.meets.entity.MeetingRoom;
import com.hsbc.meets.exception.MeetingRoomAlreadyExistsException;
import com.hsbc.meets.exception.MeetingRoomAmenitiesInvalidException;
import com.hsbc.meets.exception.MeetingRoomDoesNotExistsException;

/**
 *This class implements all the methods declared in {@link MeetingRoomDao}
 * @author ShubhraBhuniaGhosh
 *
 */
public class MeetingRoomDbDaoImpl implements MeetingRoomDao{

	private static final String TO_GET_ALL_AMENITIES_SQL = "call sp_GetAmenityName()";
	private static final String GET_AMENITY_ID_BY_AMENITY_NAME = "call sp_GetAmenityIdByAmenityName();";
	private static final String DELETE_AMENITIES_BY_MEETING_ROOM_ID_SQL = "call sp_DeleteAmenitiesByMeetingRoomId(?)";
	private static final String SELECT_MEETINGROOM_ID_FROM_MEETINGROOM_BY_NAME_SQL = "call sp_SelectMeetingRoomIdFromMeetingRoomByName(?)";
	private static final String INSERT_AMENITY_IN_MEETING_ROOM_AMENITIES_SQL = "call sp_InsertAmenityInMeetingRoomAmenities(?,?)";
	private static final String UPDATE_MEETING_ROOM_BY_ID_SQL = "call sp_UpdateMeetingRoomById(?,?,?)";
	private static final String INSERT_MEETING_ROOM_SQL = "call sp_AddMeetingRoom(?,?)";
	private static final String SELECT_ALL_ROOMS_SQL = "call sp_DisplayMeetingRoomExceptId()";
	/**
	 * Database credentials 
	 */
	private static final String USER_NAME = "root";
	private static final String PASSWORD = "root123";	
	private static final String DRIVER_CLASS_NAME = "com.mysql.cj.jdbc.Driver";
	private static final String URL = "jdbc:mysql://localhost:3306/project";

	private Connection con;
	
	/**
	 * @author ShubhraBhuniaGhosh
	 */
	public MeetingRoomDbDaoImpl(){
		con = null;
		try
		{
			Class.forName(DRIVER_CLASS_NAME);
			con = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
		}
		catch (SQLException | ClassNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * @author ShubhraBhuniaGhosh
	 */
	protected void finalize()  {
		try {
			if(con!=null) {
				con.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addMeetingRoom(MeetingRoom meetingRoom) throws MeetingRoomAlreadyExistsException{
		try (
				CallableStatement stmt  = con.prepareCall(INSERT_MEETING_ROOM_SQL);			
		)
		{
			stmt.setString(1, meetingRoom.getMeetingRoomName());
			stmt.setInt(2, meetingRoom.getSeatingCapacity());
			
			stmt.execute();

			/**
			 * NOTE : From Alan
			 * 
			 * When we implement this using stored procedures
			 * We will return a meeting room on room creation so that we have the
			 * meeting room ID
			 * 
			 */
			
		} catch (SQLIntegrityConstraintViolationException se) {
			throw new MeetingRoomAlreadyExistsException();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	/**
	 * @author ShubhraBhuniaGhosh
	 */
	public int updateMeetingRoomById(MeetingRoom newMeetingRoom) throws MeetingRoomDoesNotExistsException {
		CallableStatement stmt = null;
		int numberOfRowsUpdate  = -1;
		try {
			stmt = con.prepareCall(UPDATE_MEETING_ROOM_BY_ID_SQL);
			stmt.setString(1, newMeetingRoom.getMeetingRoomName());
			stmt.setInt(2, newMeetingRoom.getSeatingCapacity());
			numberOfRowsUpdate = stmt.executeUpdate();
			if(numberOfRowsUpdate==0) {
				throw new MeetingRoomDoesNotExistsException();
			}
		}catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			try
			{
				if(stmt != null)
				{
					stmt.close();
				}
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return numberOfRowsUpdate;
	}

	/**
	 * @author ShubhraBhuniaGhosh
	 */
	public int deleteAmenitiesByMeetingRoomById(int meetingRoomId) {
		CallableStatement stmt = null;
		int numberOfRowsUpdate  = -1;
		try {
			stmt = con.prepareCall(DELETE_AMENITIES_BY_MEETING_ROOM_ID_SQL);
			stmt.setInt(1,meetingRoomId);
			numberOfRowsUpdate = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally
		{
			try
			{
				if(stmt != null)
				{
					stmt.close();
				}
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return numberOfRowsUpdate;
	}
	/**
	 * @author ShubhraBhuniaGhosh
	 */
	public int getAmenityIdByAmenityName(String amenityName) throws MeetingRoomAmenitiesInvalidException{
		CallableStatement stmt = null;
		ResultSet resultSet = null;
		int amenityId = -1;
		try {
			stmt = con.prepareCall(GET_AMENITY_ID_BY_AMENITY_NAME);
			stmt.setString(1, amenityName.toLowerCase());
			resultSet = stmt.executeQuery();
			if(resultSet.next()) {
				amenityId = resultSet.getInt(1);
			}else {
				throw new MeetingRoomAmenitiesInvalidException();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{	
				if(resultSet!=null)
				{
					resultSet.close();
				}

				if(stmt != null)
				{
					stmt.close();
				}
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return amenityId;

	}
	
	/**
	 * @author ShubhraBhuniaGhosh
	 */
	public int insertAmenitiesInAmenityMeetingRoomById(int meetingRoomId, List<String> amenities) throws MeetingRoomAmenitiesInvalidException{
		CallableStatement stmt = null;
		int numberOfRowsUpdate  = -1;
//		int amenityId = getAmenityIdByAmenityName(amenityName);
//		try {
//			stmt = con.prepareCall(INSERT_AMENITY_IN_MEETING_ROOM_AMENITIES_SQL);
//			stmt.setInt(1,amenityId);
//			stmt.setInt(2, meetingRoomId);
//			numberOfRowsUpdate = stmt.executeUpdate();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}finally
//		{
//			try
//			{
//				if(stmt != null)
//				{
//					stmt.close();
//				}
//			}
//			catch (SQLException e) 
//			{
//				e.printStackTrace();
//			}
//		}
		return numberOfRowsUpdate;
	}


	/**
	 * @author ShubhraBhuniaGhosh
	 */
	public boolean checkMeetingRoomNameAlreadyExists(String meetingRoomName,int meetingRoomId) throws MeetingRoomAlreadyExistsException{
		CallableStatement stmt = null;
		ResultSet resultSet = null;
		try {
			stmt = con.prepareCall(SELECT_MEETINGROOM_ID_FROM_MEETINGROOM_BY_NAME_SQL);
			stmt.setString(1, meetingRoomName);
			resultSet = stmt.executeQuery();
			if(resultSet.next()) {
				if(resultSet.getInt(1)!=meetingRoomId) {
					throw new MeetingRoomAlreadyExistsException();
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if(resultSet!=null)
				{
					resultSet.close();
				}

				if(stmt != null)
				{
					stmt.close();
				}
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * @author ShubhraBhuniaGhosh
	 */
	public List<String> getAllAmenities(){
		List<String> amenities = null;
		ResultSet resultSet = null;
		CallableStatement stmt = null;
		try {
			stmt = con.prepareCall(TO_GET_ALL_AMENITIES_SQL);
			resultSet = stmt.executeQuery();
			amenities = new ArrayList<String>();
			while(resultSet.next()) {
				amenities.add(resultSet.getString(1));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally
		{
			try
			{	
				if(resultSet!=null)
				{
					resultSet.close();
				}

				if(stmt != null)
				{
					stmt.close();
				}
			}
			catch (SQLException e) 
			{
				e.printStackTrace();
			}
		}
		return amenities;
	}
	
	@Override
	public List<MeetingRoom> showAllMeetingRooms() {
		List<MeetingRoom> roomList = new ArrayList<>();
		CallableStatement stmt = null;
		ResultSet rs = null;
		try 
		{
			stmt=con.prepareCall(SELECT_ALL_ROOMS_SQL); 
		    rs = stmt.executeQuery();
			while (rs.next()) {

				/*Dummy value till we change dao 
				 */
				MeetingRoom meet = new MeetingRoom(
						"name",
						10,
						new ArrayList<String>()
						);

				roomList.add(meet);		
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if(stmt != null)
				try {
					stmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
            if(rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		
		return roomList;
	}

}





