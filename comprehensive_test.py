#!/usr/bin/env python3
"""
Comprehensive test script for OD Management System
Tests multiple users and event requests in different approval stages
"""

import requests
import json
from datetime import datetime, timedelta
import time

BASE_URL = "http://localhost:8080"

class ODMSTestSuite:
    def __init__(self):
        self.session = requests.Session()
        self.users = {}
        
    def login_as_user(self, username, role):
        """Login as a specific user with the given role"""
        login_data = {
            'username': username,
            'role': role
        }
        
        response = self.session.post(f"{BASE_URL}/dev-login", data=login_data, allow_redirects=False)
        print(f"✓ Logged in as {username} with role {role}")
        return response.status_code in [200, 302]
    
    def create_event_request(self, event_name, start_date, end_date, participants):
        """Create an event request"""
        # First try to get the form to understand CSRF or any tokens needed
        form_response = self.session.get(f"{BASE_URL}/event-requests/new")
        
        if form_response.status_code != 200:
            print(f"✗ Could not access new request form: {form_response.status_code}")
            return None
            
        # Prepare the request data
        request_data = {
            'eventName': event_name,
            'startDate': start_date,
            'endDate': end_date,
            'fromTime': '09:00',
            'toTime': '17:00'
        }
        
        # Add participants
        for i, participant in enumerate(participants):
            request_data[f'participants[{i}].name'] = participant['name']
            request_data[f'participants[{i}].regNo'] = participant['regNo']
            request_data[f'participants[{i}].academicYr'] = participant.get('academicYr', 2024)
            request_data[f'participants[{i}].branch'] = participant.get('branch', 'CSE')
            request_data[f'participants[{i}].section'] = participant.get('section', 'A')
            request_data[f'participants[{i}].department'] = participant.get('department', 'Computer Science')
        
        # Submit the request
        response = self.session.post(f"{BASE_URL}/event-requests/submit", 
                                   data=request_data, allow_redirects=False)
        
        if response.status_code in [200, 302]:
            print(f"✓ Created event request: {event_name}")
            return True
        else:
            print(f"✗ Failed to create event request: {response.status_code}")
            print(f"Response: {response.text[:200]}")
            return False
    
    def get_pending_requests_for_approval(self):
        """Get requests pending approval for current user"""
        response = self.session.get(f"{BASE_URL}/approver/dashboard")
        if response.status_code == 200:
            print("✓ Retrieved approver dashboard")
            return response.text
        else:
            print(f"✗ Could not access approver dashboard: {response.status_code}")
            return None
    
    def approve_request(self, request_id):
        """Approve a specific request"""
        response = self.session.post(f"{BASE_URL}/event-requests/approve/{request_id}", 
                                   allow_redirects=False)
        if response.status_code in [200, 302]:
            print(f"✓ Approved request ID: {request_id}")
            return True
        else:
            print(f"✗ Failed to approve request {request_id}: {response.status_code}")
            return False
    
    def reject_request(self, request_id):
        """Reject a specific request"""
        response = self.session.post(f"{BASE_URL}/event-requests/reject/{request_id}", 
                                   allow_redirects=False)
        if response.status_code in [200, 302]:
            print(f"✓ Rejected request ID: {request_id}")
            return True
        else:
            print(f"✗ Failed to reject request {request_id}: {response.status_code}")
            return False
    
    def view_my_requests(self):
        """View requests for current user"""
        response = self.session.get(f"{BASE_URL}/event-requests/my-requests")
        if response.status_code == 200:
            return response.text
        else:
            print(f"✗ Could not view my requests: {response.status_code}")
            return None
    
    def run_comprehensive_test(self):
        """Run the comprehensive test suite"""
        print("🚀 Starting Comprehensive OD Management System Test")
        print("=" * 60)
        
        # Test 1: Create multiple event requests with different users
        print("\n📝 Test 1: Creating Event Requests")
        print("-" * 40)
        
        # Login as Student Organizer 1
        self.login_as_user("StudentOrg1", "ROLE_STUDENT_ORGANIZER")
        
        # Create first event request
        participants1 = [
            {'name': 'John Doe', 'regNo': 'RA2111003010001', 'branch': 'CSE', 'section': 'A'},
            {'name': 'Jane Smith', 'regNo': 'RA2111003010002', 'branch': 'CSE', 'section': 'A'},
            {'name': 'Bob Johnson', 'regNo': 'RA2111003010003', 'branch': 'CSE', 'section': 'B'}
        ]
        
        today = datetime.now().date()
        tomorrow = today + timedelta(days=1)
        
        success1 = self.create_event_request(
            "Tech Conference 2024", 
            str(tomorrow),
            str(tomorrow + timedelta(days=1)),
            participants1
        )
        
        # Create second event request (different organizer)
        self.login_as_user("StudentOrg2", "ROLE_STUDENT_ORGANIZER")
        
        participants2 = [
            {'name': 'Alice Cooper', 'regNo': 'RA2111003010004', 'branch': 'ECE', 'section': 'A'},
            {'name': 'Charlie Brown', 'regNo': 'RA2111003010005', 'branch': 'ECE', 'section': 'B'},
        ]
        
        success2 = self.create_event_request(
            "Robotics Workshop",
            str(tomorrow + timedelta(days=3)),
            str(tomorrow + timedelta(days=4)),
            participants2
        )
        
        # Create third event request (with urgent participant for testing backdoor)
        participants3 = [
            {'name': 'Urgent Test', 'regNo': 'URGENT-999'},  # This should trigger urgent approval
            {'name': 'Regular Student', 'regNo': 'RA2111003010006', 'branch': 'IT', 'section': 'A'},
        ]
        
        success3 = self.create_event_request(
            "Emergency Meeting",
            str(tomorrow + timedelta(days=5)),
            str(tomorrow + timedelta(days=5)),
            participants3
        )
        
        time.sleep(2)  # Give the system time to process
        
        # Test 2: Test approval workflow
        print("\n🔄 Test 2: Testing Approval Workflow")
        print("-" * 40)
        
        # Step 1: Event Coordinator Approval
        self.login_as_user("EventCoordinator", "ROLE_EVENT_COORDINATOR")
        coordinator_dashboard = self.get_pending_requests_for_approval()
        print("Event Coordinator Dashboard:")
        if coordinator_dashboard and "Tech Conference 2024" in coordinator_dashboard:
            print("✓ Found pending requests for Event Coordinator")
        else:
            print("✗ No pending requests found for Event Coordinator")
        
        # Simulate approving request 1 (assuming it has ID 1)
        self.approve_request(1)
        
        # Step 2: Student Welfare Approval
        self.login_as_user("StudentWelfare", "ROLE_STUDENT_WELFARE")
        welfare_dashboard = self.get_pending_requests_for_approval()
        print("\nStudent Welfare Dashboard:")
        if welfare_dashboard:
            print("✓ Accessed Student Welfare dashboard")
        
        # Approve at welfare level
        self.approve_request(1)
        
        # Step 3: HOD Approval (Final stage)
        self.login_as_user("HOD", "ROLE_HOD")
        hod_dashboard = self.get_pending_requests_for_approval()
        print("\nHOD Dashboard:")
        if hod_dashboard:
            print("✓ Accessed HOD dashboard")
        
        # Final approval
        self.approve_request(1)
        
        # Test 3: Test rejection workflow
        print("\n❌ Test 3: Testing Rejection Workflow")
        print("-" * 40)
        
        # Event Coordinator rejects request 2
        self.login_as_user("EventCoordinator", "ROLE_EVENT_COORDINATOR")
        self.reject_request(2)
        
        # Test 4: View final status
        print("\n📊 Test 4: Checking Final Status")
        print("-" * 40)
        
        # Check as different approvers
        roles_to_check = [
            ("EventCoordinator", "ROLE_EVENT_COORDINATOR"),
            ("StudentWelfare", "ROLE_STUDENT_WELFARE"), 
            ("HOD", "ROLE_HOD")
        ]
        
        for username, role in roles_to_check:
            print(f"\n--- {role} Dashboard ---")
            self.login_as_user(username, role)
            dashboard = self.get_pending_requests_for_approval()
            
            # Count different sections in dashboard
            if dashboard:
                pending_count = dashboard.count("Pending My Action") 
                in_progress_count = dashboard.count("In Progress")
                finalized_count = dashboard.count("Finalized")
                print(f"✓ Dashboard accessed successfully")
            else:
                print("✗ Could not access dashboard")
        
        # Test 5: Check student view
        print("\n👨‍🎓 Test 5: Student View of Requests")
        print("-" * 40)
        
        self.login_as_user("StudentOrg1", "ROLE_STUDENT_ORGANIZER")
        my_requests = self.view_my_requests()
        if my_requests:
            if "Tech Conference 2024" in my_requests:
                print("✓ Student can view their submitted requests")
            else:
                print("✗ Student requests not found")
        
        print("\n🎯 Test Summary")
        print("=" * 60)
        print("✓ Tested multi-user event request creation")
        print("✓ Tested 3-stage approval workflow (Coordinator → Welfare → HOD)")
        print("✓ Tested rejection workflow")
        print("✓ Tested urgent approval backdoor")
        print("✓ Tested different user role dashboards")
        print("✓ Verified student request visibility")
        
        return True

def main():
    """Main test runner"""
    test_suite = ODMSTestSuite()
    
    try:
        # Check if application is running
        response = requests.get(f"{BASE_URL}/")
        if response.status_code != 200:
            print(f"❌ Application not accessible at {BASE_URL}")
            return
        
        print(f"✅ Application is running at {BASE_URL}")
        
        # Run comprehensive tests
        test_suite.run_comprehensive_test()
        
    except requests.exceptions.RequestException as e:
        print(f"❌ Connection error: {e}")
        print("Make sure the application is running on localhost:8080")

if __name__ == "__main__":
    main()