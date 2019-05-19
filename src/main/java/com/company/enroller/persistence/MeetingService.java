package com.company.enroller.persistence;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;

import org.hibernate.Query;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component("meetingService")
public class MeetingService {

    DatabaseConnector connector;
    
    @Autowired
    ParticipantService participantService;

    public MeetingService() {
        connector = DatabaseConnector.getInstance();
    }

    public Collection<Meeting> getAll() {
        String hql = "FROM Meeting";
        Query query = connector.getSession().createQuery(hql);
        return query.list();
    }
    
    public Meeting findById(long meetingId) {
		String hql = "FROM Meeting as mt where mt.id =" + meetingId;
		Query query = connector.getSession().createQuery(hql);
		return (Meeting) query.uniqueResult();
	}
    
    public void add(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().save(meeting);
		transaction.commit();
	}
    
    public void deleteMeeting(Meeting meeting) {
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().delete(meeting);
		transaction.commit();
	}
    
    public Participant addParticipantToMeeting(Meeting meeting, String login) {
    	Participant participant = participantService.findByLogin(login);
		meeting.addParticipant(participant);
		Transaction transaction = connector.getSession().beginTransaction();
		connector.getSession().update(meeting);
		transaction.commit();
		return participant;
	}


}
