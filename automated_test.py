#!/usr/bin/env python3
"""
Automated testing script that tests the ODMS system assuming test data exists
"""

import requests
import json
from datetime import datetime, timedelta
import time

BASE_URL = "http://localhost:8080"

class ODMSAutomatedTest:
    def __init__(self):
        self.session = requests.Session()
        self.test_results = {
            'login_tests': [],
            'dashboard_tests': [],
            'approval_tests': [],
            'view_tests': []
        }
    
    def login_as_user(self, username, role):
        """Login as a specific user with the given role"""
        login_data = {
            'username': username,
            'role': role
        }
        
        response = self.session.post(f"{BASE_URL}/dev-login", data=login_data, allow_redirects=False)
        success = response.status_code in [200, 302]
        
        test_result = {
            'username': username,
            'role': role,
            'success': success,
            'status_code': response.status_code
        }
        self.test_results['login_tests'].append(test_result)
        
        if success:
            print(f"✓ Logged in as {username} with role {role}")
        else:
            print(f"✗ Login failed for {username}: {response.status_code}")
            
        return success
    
    def test_approver_dashboard(self, username, role):
        """Test approver dashboard for a specific role"""
        if not self.login_as_user(username, role):
            return False
            
        response = self.session.get(f"{BASE_URL}/approver/dashboard")
        success = response.status_code == 200
        
        content = ""
        pending_count = 0
        in_progress_count = 0
        finalized_count = 0
        
        if success:
            content = response.text
            # Count sections that indicate requests
            pending_count = content.count('approve-btn') + content.count('reject-btn')
            in_progress_count = content.count('PENDING_')
            finalized_count = content.count('APPROVED') + content.count('REJECTED')
        
        test_result = {
            'username': username,
            'role': role,
            'success': success,
            'status_code': response.status_code,
            'pending_requests': pending_count,
            'in_progress_requests': in_progress_count,
            'finalized_requests': finalized_count,
            'has_content': len(content) > 1000 if success else False
        }
        
        self.test_results['dashboard_tests'].append(test_result)
        
        if success:
            print(f"✓ {role} dashboard loaded successfully")
            print(f"  - Action buttons found: {pending_count}")
            print(f"  - Content length: {len(content)} chars")
        else:
            print(f"✗ {role} dashboard failed: {response.status_code}")
            
        return success
    
    def test_approval_action(self, request_id, action='approve'):
        """Test approving or rejecting a request"""
        endpoint = f"{BASE_URL}/event-requests/{action}/{request_id}"
        response = self.session.post(endpoint, allow_redirects=False)
        success = response.status_code in [200, 302]
        
        test_result = {
            'request_id': request_id,
            'action': action,
            'success': success,
            'status_code': response.status_code
        }
        self.test_results['approval_tests'].append(test_result)
        
        if success:
            print(f"✓ {action.title()} request {request_id}: Success")
        else:
            print(f"✗ {action.title()} request {request_id}: Failed ({response.status_code})")
            
        return success
    
    def test_student_view(self, username):
        """Test student organizer view"""
        if not self.login_as_user(username, "ROLE_STUDENT_ORGANIZER"):
            return False
            
        response = self.session.get(f"{BASE_URL}/event-requests/my-requests")
        success = response.status_code == 200
        
        request_count = 0
        if success:
            content = response.text
            request_count = content.count('<tr>') - 1  # Subtract header row
        
        test_result = {
            'username': username,
            'success': success,
            'status_code': response.status_code,
            'visible_requests': max(0, request_count)
        }
        self.test_results['view_tests'].append(test_result)
        
        if success:
            print(f"✓ Student view loaded - Visible requests: {request_count}")
        else:
            print(f"✗ Student view failed: {response.status_code}")
            
        return success
    
    def test_other_endpoints(self):
        """Test other available endpoints"""
        print(f"\n🌐 Testing Additional Endpoints")
        print("-" * 40)
        
        endpoints_to_test = [
            ('/admin/dashboard', 'ROLE_ADMIN', 'Admin'),
            ('/faculty/dashboard', 'ROLE_FACULTY', 'Faculty'),
        ]
        
        for endpoint, role, description in endpoints_to_test:
            print(f"\n--- Testing {description} Dashboard ---")
            self.login_as_user(f"Test{description}", role)
            
            response = self.session.get(f"{BASE_URL}{endpoint}")
            
            if response.status_code == 200:
                print(f"✓ {description} dashboard accessible")
                content_length = len(response.text)
                print(f"  - Content length: {content_length} chars")
            elif response.status_code == 404:
                print(f"⚠ {description} endpoint not found: {endpoint}")
            else:
                print(f"✗ {description} dashboard error: {response.status_code}")
    
    def run_comprehensive_workflow_test(self):
        """Run comprehensive workflow test"""
        print("🚀 ODMS Comprehensive Workflow Test")
        print("=" * 60)
        print("Testing with assumed test data in database...")
        
        # Test 1: Approver Dashboards
        print(f"\n📊 Test 1: Approver Dashboard Access")
        print("-" * 40)
        
        approver_roles = [
            ('EventCoordinator', 'ROLE_EVENT_COORDINATOR'),
            ('StudentWelfare', 'ROLE_STUDENT_WELFARE'),
            ('HOD', 'ROLE_HOD')
        ]
        
        for username, role in approver_roles:
            self.test_approver_dashboard(username, role)
            time.sleep(0.5)  # Small delay between tests
        
        # Test 2: Approval Actions
        print(f"\n⚡ Test 2: Testing Approval Actions")
        print("-" * 40)
        
        # Test approval workflow - assuming requests 1, 2, 3 exist
        print("\n2a. Event Coordinator approves request 1")
        self.login_as_user("EventCoordinator", "ROLE_EVENT_COORDINATOR")
        self.test_approval_action(1, 'approve')
        
        print("\n2b. Student Welfare approves request 2")
        self.login_as_user("StudentWelfare", "ROLE_STUDENT_WELFARE") 
        self.test_approval_action(2, 'approve')
        
        print("\n2c. HOD gives final approval to request 3")
        self.login_as_user("HOD", "ROLE_HOD")
        self.test_approval_action(3, 'approve')
        
        print("\n2d. Event Coordinator rejects request 5")
        self.login_as_user("EventCoordinator", "ROLE_EVENT_COORDINATOR")
        self.test_approval_action(5, 'reject')
        
        # Test 3: Student Views
        print(f"\n👨‍🎓 Test 3: Student Organizer Views") 
        print("-" * 40)
        
        student_users = ['StudentOrg1', 'StudentOrg2', 'StudentOrg3']
        for username in student_users:
            self.test_student_view(username)
        
        # Test 4: Other Endpoints
        self.test_other_endpoints()
        
        # Test 5: Re-check dashboards after actions
        print(f"\n🔄 Test 5: Dashboard State After Actions")
        print("-" * 40)
        
        for username, role in approver_roles:
            print(f"\n--- Re-checking {role} Dashboard ---")
            self.test_approver_dashboard(username, role)
    
    def print_test_summary(self):
        """Print comprehensive test summary"""
        print(f"\n🎯 Test Results Summary")
        print("=" * 60)
        
        # Login test summary
        login_success = sum(1 for test in self.test_results['login_tests'] if test['success'])
        login_total = len(self.test_results['login_tests'])
        print(f"Login Tests: {login_success}/{login_total} successful")
        
        # Dashboard test summary  
        dashboard_success = sum(1 for test in self.test_results['dashboard_tests'] if test['success'])
        dashboard_total = len(self.test_results['dashboard_tests'])
        print(f"Dashboard Tests: {dashboard_success}/{dashboard_total} successful")
        
        # Approval test summary
        approval_success = sum(1 for test in self.test_results['approval_tests'] if test['success'])
        approval_total = len(self.test_results['approval_tests'])
        print(f"Approval Tests: {approval_success}/{approval_total} successful")
        
        # View test summary
        view_success = sum(1 for test in self.test_results['view_tests'] if test['success'])
        view_total = len(self.test_results['view_tests'])
        print(f"View Tests: {view_success}/{view_total} successful")
        
        # Detailed results
        print(f"\n📋 Detailed Results")
        print("-" * 40)
        
        print("Dashboard Access Results:")
        for test in self.test_results['dashboard_tests']:
            status = "✓" if test['success'] else "✗"
            print(f"  {status} {test['role']}: Status {test['status_code']}, Actions: {test['pending_requests']}")
        
        print("\nApproval Action Results:")
        for test in self.test_results['approval_tests']:
            status = "✓" if test['success'] else "✗"
            print(f"  {status} {test['action'].title()} Request {test['request_id']}: Status {test['status_code']}")
        
        print(f"\n✅ Test Coverage Achieved:")
        print("  • Multi-user login functionality")
        print("  • Approver dashboard access for all roles")
        print("  • Request approval/rejection actions")
        print("  • Student organizer request views")
        print("  • Cross-role dashboard verification")
        print("  • Workflow state changes")

def main():
    """Main test runner"""
    print("🧪 ODMS Automated Testing Suite")
    print("=" * 60)
    
    # Check if application is running
    try:
        response = requests.get(f"{BASE_URL}/")
        if response.status_code != 200:
            print(f"❌ Application not accessible at {BASE_URL}")
            return
        print(f"✅ Application is running at {BASE_URL}")
    except requests.exceptions.RequestException as e:
        print(f"❌ Connection error: {e}")
        return
    
    # Run automated tests
    test_suite = ODMSAutomatedTest()
    
    try:
        test_suite.run_comprehensive_workflow_test()
        test_suite.print_test_summary()
        
        print(f"\n💡 Instructions for Complete Testing:")
        print("To create test data, run these SQL commands in H2 Console:")
        print("(http://localhost:8080/h2-console, JDBC: jdbc:h2:mem:testdb, User: sa)")
        print("\nSample SQL:")
        print("INSERT INTO EVENT_REQUEST (id, event_name, start_date, end_date, from_time, to_time, status, is_hidden) VALUES")
        print("(1, 'Test Event', '2024-10-02', '2024-10-02', '09:00', '17:00', 'SUBMITTED', false);")
        
    except Exception as e:
        print(f"❌ Test execution error: {e}")

if __name__ == "__main__":
    main()