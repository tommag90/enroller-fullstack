package com.company.enroller.controllers;

import com.company.enroller.model.Meeting;
import com.company.enroller.model.Participant;
import com.company.enroller.persistence.MeetingService;
import com.company.enroller.persistence.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/api/meetings")
public class MeetingRestController {

    @Autowired
    MeetingService meetingService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getMeetings() {

        Collection<Meeting> meetings = meetingService.getAll();
        return new ResponseEntity<Collection<Meeting>>(meetings, HttpStatus.OK);
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST)
	public ResponseEntity<?> addMeeting(@RequestBody Meeting meeting) {
		Meeting foundMeeting = meetingService.findById(meeting.getId());
		if(foundMeeting != null) {
			return new ResponseEntity("Unable to create. A meeting with id " + meeting.getId() + " already exist.", HttpStatus.CONFLICT);
		}
		meetingService.add(meeting);
		return new ResponseEntity<Meeting>(meeting, HttpStatus.OK);
	}
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<?> deleteMeeting(@PathVariable("id") long meetingId) {
		Meeting meeting = meetingService.findById(meetingId);
		if (meeting == null) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		meetingService.deleteMeeting(meeting);
		return new ResponseEntity("Meeting " + meeting.getId() +
				" deleted.", HttpStatus.OK);
	}
    
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.POST)
    public ResponseEntity<?> addParticipantToMeeting(@PathVariable("id") long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        String particLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Participant participant = meetingService.addParticipantToMeeting(meeting, particLogin);
        return new ResponseEntity<>(participant, HttpStatus.CREATED);
    }
    
    @RequestMapping(value = "/{id}/participants", method = RequestMethod.DELETE)
    public ResponseEntity<?> removeParticipantFromMeeting(@PathVariable("id") long meetingId) {
        Meeting meeting = meetingService.findById(meetingId);
        if (meeting == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        String particLogin = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        meetingService.removeParticipantFromMeeting(meeting, particLogin);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
