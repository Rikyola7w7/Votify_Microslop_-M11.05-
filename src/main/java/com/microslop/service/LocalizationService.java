package com.microslop.service;

import com.vaadin.flow.server.VaadinSession;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LocalizationService {

    private static final String SESSION_KEY = "locale";
    public static final String ENGLISH = "en";
    public static final String SPANISH = "es";

    private static final Map<String, Map<String, String>> translations = new HashMap<>();

    static {
        Map<String, String> en = new HashMap<>();
        en.put("app.title", "Votify");

        en.put("nav.signin", "Sign In");
        en.put("nav.register", "Register");
        en.put("nav.signout", "Sign Out");
        en.put("nav.myprojects", "My Projects");
        en.put("nav.mycompetitions", "My Competitions");
        en.put("nav.invitations", "Invitations");
        en.put("nav.editprofile", "Edit Profile");
        en.put("nav.notifications", "Notifications");
        en.put("nav.viewallnotifications", "View All Notifications");
        en.put("nav.nonotifications", "No recent notifications");
        en.put("nav.errorloadingnotifications", "Error loading notifications");

        en.put("home.discover", "Discover Competitions");
        en.put("home.findvote", "Find and vote for the best projects");
        en.put("home.filter.all", "All");
        en.put("home.filter.active", "Active");
        en.put("home.filter.finished", "Finished");
        en.put("home.search.placeholder", "Search competitions...");
        en.put("home.nocompetitions", "No competitions found");
        en.put("home.nomatching", "There are no competitions matching your criteria.");
        en.put("home.errorloading", "Error loading competitions: ");

        en.put("login.welcome", "Welcome back");
        en.put("login.signincontinue", "Sign in to continue");
        en.put("login.username", "Username");
        en.put("login.username.placeholder", "Enter your username");
        en.put("login.password", "Password");
        en.put("login.password.placeholder", "Enter your password");
        en.put("login.signin", "Sign In");
        en.put("login.noaccount", "Don't have an account? Register");
        en.put("login.fillall", "Please fill in all fields.");
        en.put("login.success", "Login successful!");
        en.put("login.unexpectederror", "An unexpected error occurred.");

        en.put("register.title", "Create your account");
        en.put("register.join", "Join Votify");
        en.put("register.createaccount", "Create your account");
        en.put("register.username", "Username *");
        en.put("register.username.placeholder", "Choose a username");
        en.put("register.fullname", "Full Name *");
        en.put("register.fullname.placeholder", "Enter your full name");
        en.put("register.email", "Email *");
        en.put("register.email.placeholder", "Enter your email");
        en.put("register.birthdate", "Birth Date *");
        en.put("register.password", "Password *");
        en.put("register.password.placeholder", "Create a password");
        en.put("register.confirmpassword", "Confirm Password *");
        en.put("register.confirmpassword.placeholder", "Confirm your password");
        en.put("register.createbutton", "Create Account");
        en.put("register.fillall", "Please fill in all required fields.");
        en.put("register.passwordmismatch", "Passwords do not match.");
        en.put("register.confirmtitle", "Confirm Registration");
        en.put("register.confirmmessage", "Do you want to create your account with the username \"");
        en.put("register.yes", "Yes");
        en.put("register.no", "No");
        en.put("register.success", "Account created successfully!");
        en.put("register.alreadyaccount", "Already have an account? Sign in");
        en.put("register.profilepicture", "Profile picture (optional)");
        en.put("register.dragphoto", "Drag your profile picture here");
        en.put("register.imageerror", "Error processing image.");
        en.put("register.welcome", "WELCOME ABOARD");
        en.put("register.ready", "Your account is ready — let the voting begin");

        en.put("profile.edityprofile", "Edit Profile");
        en.put("profile.signout", "Sign Out");
        en.put("profile.sessionclosed", "Session closed");
        en.put("profile.deleteaccount", "Delete Account");
        en.put("profile.mustsignin", "You must sign in");
        en.put("profile.deleteconfirmation", "Are you sure you want to delete your account? This action cannot be undone.");
        en.put("profile.cancel", "Cancel");
        en.put("profile.delete", "Delete");
        en.put("profile.accountdeletedsuccess", "Account deleted successfully");
        en.put("profile.errordeletingaccount", "Error deleting account");
        en.put("profile.profileupdated", "Profile updated");
        en.put("profile.errorupdatingprofile", "Error updating profile");

        en.put("projects.myprojects", "My Projects");
        en.put("projects.noprojectsyet", "You have no projects yet");
        en.put("projects.submitappear", "Projects you submit will appear here.");
        en.put("projects.accessdenied", "Access Denied");
        en.put("projects.onlyviewown", "You can only view your own projects.");
        en.put("projects.errorloading", "Error loading projects: ");

        en.put("voting.title", "VOTING");
        en.put("voting.category", "Category: ");
        en.put("voting.no votes remaining", "No votes remaining");
        en.put("voting.youhave", "You have ");
        en.put("voting.votesleft", " vote");
        en.put("voting.votesleft.plural", " votes left");
        en.put("voting.markchecklist", "Mark checklist items for each project");
        en.put("voting.totalvotes", "Total votes: ");
        en.put("voting.votepoints", "Vote (Checklist)");
        en.put("voting.points", "Points");
        en.put("voting.vote", "Vote");
        en.put("voting.comments", "Comments");
        en.put("voting.commentsfor", "Comments for: ");
        en.put("voting.yourcomment", "Your comment");
        en.put("voting.writefeedback", "Write your feedback here...");
        en.put("voting.save", "Save");
        en.put("voting.cancel", "Cancel");
        en.put("voting.leavefeedback", "Leave your feedback for this project.");
        en.put("voting.commentempty", "Comment cannot be empty.");
        en.put("voting.errorsavingcomment", "Error saving comment: ");
        en.put("voting.mustlogin", "You must be logged in to vote.");
        en.put("voting.selectcategory", "Please select a category before voting.");
        en.put("voting.assignpoints", "You must assign at least 1 point to vote.");
        en.put("voting.nocompetition", "This competition does not accept votes at this time.");
        en.put("voting.onlyvotes", "Error! You only have ");
        en.put("voting.votesavailable", " votes available. Cannot assign ");
        en.put("voting.points.plural", " points.");
        en.put("voting.nochecklist", "No checklist items available for this category.");
        en.put("voting.checklisterror", "Error opening checklist voting: ");
        en.put("voting.usernotfound", "User not found. Please log in again.");
        en.put("voting.nocompetitionvotes", "This competition does not accept votes at this time.");
        en.put("voting.categorynotbelong", "Category does not belong to this competition.");

        en.put("ranking.title", "RANKING");
        en.put("ranking.categories", "Categories");
        en.put("ranking.vote", "Vote");
        en.put("ranking.modifyentries", "Modify entries");
        en.put("ranking.registervoter", "Register as Voter");
        en.put("ranking.notregistered", "You are not registered as a voter for this competition. Would you like to register as a voter to participate in voting?");
        en.put("ranking.yesregister", "Yes, register me");
        en.put("ranking.nostayhere", "No, stay here");
        en.put("ranking.voterregistered", "VOTER REGISTERED");
        en.put("ranking.makevoiceheard", "Welcome aboard — time to make your voice heard");
        en.put("ranking.error", "Error: ");
        en.put("ranking.unexpectederror", "Unexpected error: ");
        en.put("ranking.start", "Start: ");
        en.put("ranking.end", "End: ");
        en.put("ranking.judgesranking", "Judges' Ranking");
        en.put("ranking.popularranking", "Popular Ranking");
        en.put("ranking.noprojectscategory", "No projects in this category");
        en.put("ranking.reclassify", "Reclassify");
        en.put("ranking.declassify", "Declassify");
        en.put("ranking.editvotes", "Edit Votes");
        en.put("ranking.newposition", "New position");
        en.put("ranking.accept", "Accept");
        en.put("ranking.projectreclassified", "Project reclassified to position ");
        en.put("ranking.confirmdeclassify", "Declassify: ");
        en.put("ranking.confirmmsg", "Are you sure you want to remove this project from the competition? This action cannot be undone. All votes and comments will be permanently deleted.");
        en.put("ranking.deletepermanently", "Delete permanently");
        en.put("ranking.projectdeclassified", "Project declassified successfully");
        en.put("ranking.newamountofvotes", "Enter the new amount of votes:");
        en.put("ranking.votes", "Votes");
        en.put("ranking.votesupdated", "Votes updated to ");
        en.put("ranking.calculating", "Calculating rankings...");
        en.put("ranking.reclassifyaction", "Reclassify: ");

        en.put("notification.title", "Notifications");
        en.put("notification.viewall", "View All");

        en.put("language.select", "Language");
        en.put("language.english", "English");
        en.put("language.spanish", "Spanish");

        en.put("competition.details", "Competition Details");
        en.put("competition.status", "Status");
        en.put("competition.categories", "Categories");
        en.put("competition.create", "Create Competition");
        en.put("competition.manage", "Manage");
        en.put("competition.configure", "Configure");
        en.put("competition.createdby", "Created by");

        en.put("projects.details.title", "Project Discussion");
        en.put("projects.details.nocomments", "No comments yet");
        en.put("projects.details.startdiscussion", "Start the discussion by leaving a comment.");

        en.put("invitations.title", "Invitations");
        en.put("invitations.accept", "Accept");
        en.put("invitations.decline", "Decline");
        en.put("invitations.pending", "Pending");
        en.put("invitations.accepted", "Accepted");
        en.put("invitations.declined", "Declined");
        en.put("invitations.invitedby", "Invited by");
        en.put("invitations.acceptedmsg", "Invitation accepted!");
        en.put("invitations.declinedmsg", "Invitation declined.");

        en.put("common.back", "Back");
        en.put("common.error", "Error");
        en.put("common.success", "Success");
        en.put("common.loading", "Loading...");
        en.put("common.na", "N/A");

        en.put("card.view", "VIEW");
        en.put("card.viewcategory", "VIEW CATEGORY");

        // --- Admin Dashboard ---
        en.put("admin.createcompetition", "Create Competition");
        en.put("admin.nocompetitions", "No competitions yet");
        en.put("admin.createfirst", "Create your first competition to get started!");
        en.put("admin.status.active", "ACTIVE");
        en.put("admin.status.finished", "FINISHED");
        en.put("admin.status.paused", "PAUSED");
        en.put("admin.eventtype", "Event Type: ");
        en.put("admin.description", "Description: ");
        en.put("admin.nodescription", "No description");
        en.put("admin.noenddate", "No end date");
        en.put("admin.enddate", "End Date: ");
        en.put("admin.configure", "Configure Competition");
        en.put("admin.manage", "Manage Competition");
        en.put("admin.accessdenied", "Access denied. You can only view your own admin dashboard.");

        // --- AI Feedback View ---
        en.put("aifeedback.back", "Back");
        en.put("aifeedback.competition", "Competition");
        en.put("aifeedback.project", "Project");
        en.put("aifeedback.generate", "Generate Feedback");
        en.put("aifeedback.lastgenerated.empty", "Last generated: --/--/----");
        en.put("aifeedback.projects", "Projects");
        en.put("aifeedback.overview", "Overview");
        en.put("aifeedback.nofeedback", "No feedback generated");
        en.put("aifeedback.selectproject.hint", "Select a project and click \"Generate Feedback\" to analyze comments with AI.");
        en.put("aifeedback.noprojects", "You have no assigned projects.");
        en.put("aifeedback.selectfirst", "Please select a project first.");
        en.put("aifeedback.generating", "Generating...");
        en.put("aifeedback.success", "Feedback generated successfully.");
        en.put("aifeedback.ratelimit", "AI request limit reached. Please wait a moment and try again.");
        en.put("aifeedback.error", "Error generating feedback: ");
        en.put("aifeedback.unavailable", "AI analysis could not be completed. The service may be temporarily unavailable. Please try again later.");
        en.put("aifeedback.section", "AI Analysis");
        en.put("aifeedback.positive", "Positive Aspects");
        en.put("aifeedback.negative", "Negative Aspects");
        en.put("aifeedback.sentiment", "Overall Sentiment");
        en.put("aifeedback.analyzed", "Comments Analyzed");
        en.put("aifeedback.positivecomments", "Positive Comments");
        en.put("aifeedback.neutralcomments", "Neutral Comments");
        en.put("aifeedback.negativecomments", "Negative Comments");
        en.put("aifeedback.distribution", "Sentiment Distribution");
        en.put("aifeedback.frequentwords", "Frequent Words");
        en.put("aifeedback.nowords.unavailable", "AI analysis could not be completed. No frequent words available.");
        en.put("aifeedback.nowords", "No significant frequent words were identified in the analyzed comments.");
        en.put("aifeedback.lastgenerated", "Last generated: ");
        en.put("aifeedback.nopoints", "No points identified.");

        // --- Category Selection View ---
        en.put("catselection.notfound", "Competition not found.");
        en.put("catselection.removed", "It may have been removed or the link is invalid.");
        en.put("catselection.backhome", "← Back to home");
        en.put("catselection.submit", "Submit Project");
        en.put("catselection.signin", "Sign in to submit a project");
        en.put("catselection.start", "Start: ");
        en.put("catselection.end", "End: ");
        en.put("catselection.categories.count", " categories");
        en.put("catselection.status.active", "ACTIVE");
        en.put("catselection.status.finished", "FINISHED");
        en.put("catselection.status.paused", "PAUSED");
        en.put("catselection.search", "Search category...");
        en.put("catselection.nocategories", "No categories found");
        en.put("catselection.nomatching", "There are no categories matching your search.");
        en.put("catselection.loading", "Loading categories...");

        // --- Certificates View ---
        en.put("cert.type", "Certificate Type");
        en.put("cert.all", "All");
        en.put("cert.participant", "Participant");
        en.put("cert.winner", "Winner");
        en.put("cert.competition", "Competition");
        en.put("cert.search", "Search");
        en.put("cert.search.placeholder", "Search by competition or project...");
        en.put("cert.refresh.label", "Refresh certificates");
        en.put("cert.refresh.title", "Refresh");
        en.put("cert.erroruser", "Unable to load current user");
        en.put("cert.loaded", "Certificates loaded successfully");
        en.put("cert.errorloading", "Error loading certificates: ");
        en.put("cert.none", "No certificates found");

        // --- Competition View ---
        en.put("compview.category", "Category:");
        en.put("compview.selectcategory", "Select a category");
        en.put("compview.general", "General");
        en.put("compview.vote", "Vote for Projects");
        en.put("compview.votetooltip", "Go to the voting page for this competition");
        en.put("compview.position4", "Position 4");
        en.put("compview.totalchecks", "Total Checks: ");
        en.put("compview.avgscore", "Avg. Score: %.1f/10");
        en.put("compview.totalvotes", "Total Votes: ");

        // --- Configure Competition View ---
        en.put("configure.accessdenied", "Access denied. Only the competition creator can configure it.");
        en.put("configure.invalidid", "Invalid competition ID.");
        en.put("configure.general", "GENERAL");
        en.put("configure.startdate", "START DATE");
        en.put("configure.starttime", "START TIME");
        en.put("configure.enddate", "END DATE");
        en.put("configure.endtime", "END TIME");
        en.put("configure.coverimage", "COVER IMAGE");
        en.put("configure.currentcover", "Current cover");
        en.put("configure.coverpreview", "Cover preview");
        en.put("configure.errorimage", "Error reading image");
        en.put("configure.categories", "CATEGORIES");
        en.put("configure.addcategory", "Add Category");
        en.put("configure.type.normal", "Normal");
        en.put("configure.type.scale", "Scale");
        en.put("configure.type.checklist", "Checklist");
        en.put("configure.deletecategory", "Delete category");
        en.put("configure.categoryremoved", "Category removed");
        en.put("configure.addnewcategory", "Add New Category");
        en.put("configure.categoryname", "Category Name");
        en.put("configure.votingtype", "Voting Type");
        en.put("configure.save", "Save");
        en.put("configure.categoryrequired", "Category name is required");
        en.put("configure.categoryadded", "Category added (pending save)");
        en.put("configure.cancel", "Cancel");
        en.put("configure.participation", "PARTICIPATION");
        en.put("configure.whocanvote", "WHO CAN VOTE");
        en.put("configure.option.judges", "Judges");
        en.put("configure.option.everyone", "Everyone");
        en.put("configure.autovote", "AUTO VOTE");
        en.put("configure.option.off", "OFF");
        en.put("configure.option.on", "ON");
        en.put("configure.votesperperson", "VOTES PER PERSON");
        en.put("configure.addjudges", "ADD JUDGES");
        en.put("configure.addjudge", "Add Judge");
        en.put("configure.removejudge", "Remove judge");
        en.put("configure.addnewjudge", "Add New Judge");
        en.put("configure.judgeusername", "Judge Username");
        en.put("configure.enterusername", "Enter username");
        en.put("configure.judgerequired", "Please enter a judge username");
        en.put("configure.usernotfound", "User not found: ");
        en.put("configure.alreadyjudge", "This user is already a judge in this competition");
        en.put("configure.judgepending", "Judge pending save");
        en.put("configure.alreadyadded", "This user has already been added");
        en.put("configure.pending", "(Pending)");
        en.put("configure.removependingjudge", "Remove pending judge");
        en.put("configure.votetype", "VOTE TYPE");
        en.put("configure.votingmode", "VOTING MODE");
        en.put("configure.mode.normal", "Normal");
        en.put("configure.mode.checklist", "Checklist");
        en.put("configure.mode.scale", "Scale (0-10)");
        en.put("configure.checklistitems", "CHECKLIST ITEMS");
        en.put("configure.addchecklistitem", "Add Checklist Item");
        en.put("configure.deletechecklistitem", "Delete checklist item");
        en.put("configure.addnewchecklistitem", "Add New Checklist Item");
        en.put("configure.itemdescription", "Item Description");
        en.put("configure.itemrequired", "Item description is required");
        en.put("configure.scaleconfig", "SCALE CONFIGURATION");
        en.put("configure.scalefixed", "Scale range is fixed: 0 - 10");
        en.put("configure.comments", "COMMENTS");
        en.put("configure.allowcomments", "ALLOW COMMENTS");
        en.put("configure.option.yes", "YES");
        en.put("configure.option.no", "NO");
        en.put("configure.requiredcomments", "REQUIRED COMMENTS");
        en.put("configure.confirmexit", "Confirm Exit");
        en.put("configure.exitmsg", "Are you sure you want to exit without saving changes?");
        en.put("configure.exitwithout", "Exit Without Saving");
        en.put("configure.continueediting", "Continue Editing");
        en.put("configure.minvotes", "Error: Max votes per person must be at least 1.");
        en.put("configure.errordeletecategories", "Error deleting categories: ");
        en.put("configure.errorprocesscategories", "Error processing category changes: ");
        en.put("configure.errorprocessjudges", "Error processing judge changes: ");
        en.put("configure.saved", "Configuration saved successfully");
        en.put("configure.errorsave", "Error saving configuration: ");

        // --- Create Competition View ---
        en.put("createcomp.accessdenied", "Access denied. You can only create competitions for your own account.");
        en.put("createcomp.name", "Competition Name *");
        en.put("createcomp.name.placeholder", "Enter competition name (max 20 characters)");
        en.put("createcomp.description", "Description");
        en.put("createcomp.description.placeholder", "Enter competition description");
        en.put("createcomp.eventtype", "Event Type *");
        en.put("createcomp.type.tech", "Tech");
        en.put("createcomp.type.art", "Art");
        en.put("createcomp.type.music", "Music");
        en.put("createcomp.type.sports", "Sports");
        en.put("createcomp.type.business", "Business");
        en.put("createcomp.type.education", "Education");
        en.put("createcomp.type.other", "Other");
        en.put("createcomp.startdate", "Start Date *");
        en.put("createcomp.enddate", "End Date *");
        en.put("createcomp.coverimage", "Cover Image (Optional)");
        en.put("createcomp.coverpreview", "Cover preview");
        en.put("createcomp.errorimage", "Error reading image");
        en.put("createcomp.categories", "Categories");
        en.put("createcomp.categoryname", "Category Name");
        en.put("createcomp.categoryname.placeholder", "e.g., Gaming, Design, etc.");
        en.put("createcomp.votingtype", "Voting Type");
        en.put("createcomp.type.normal", "Normal");
        en.put("createcomp.type.scale", "Scale");
        en.put("createcomp.type.checklist", "Checklist");
        en.put("createcomp.errorcatimage", "Error reading category image");
        en.put("createcomp.addcategory", "Add Category");
        en.put("createcomp.zerocategories", "0 categories added");
        en.put("createcomp.judges", "Judges");
        en.put("createcomp.judgeusername", "Judge Username");
        en.put("createcomp.judgeusername.placeholder", "Enter judge username");
        en.put("createcomp.addjudge", "Add Judge");
        en.put("createcomp.cancel", "Cancel");
        en.put("createcomp.create", "Create");
        en.put("createcomp.catnamerequired", "Please enter a category name.");
        en.put("createcomp.catalready", "Category already added.");
        en.put("createcomp.categories.count.singular", " category added");
        en.put("createcomp.categories.count.plural", " categories added");
        en.put("createcomp.judgerequired", "Please enter a judge username.");
        en.put("createcomp.judgenotfound", "Judge user not found: ");
        en.put("createcomp.judgealready", "Judge already added.");
        en.put("createcomp.mustlogin", "You must be logged in to create a competition.");
        en.put("createcomp.success", "Competition created successfully!");
        en.put("createcomp.error", "An error occurred while creating the competition.");

        // --- FAQ View ---
        en.put("faq.q1", "What is Votify?");
        en.put("faq.a1", "Votify is an academic web platform for managing competitions and evaluating projects. It supports multiple voting modalities (classic, scale, and checklist), AI-powered feedback, and dynamic certificate generation.");
        en.put("faq.q2", "How does voting work?");
        en.put("faq.a2", "Once registered as a voter, you can browse active competitions, select a category, and cast your votes on the available projects. Each competition may have a limited number of votes per participant.");
        en.put("faq.q3", "What are the different vote types?");
        en.put("faq.a3", "Votify supports three vote types: Normal (classic popularity vote), Scale (rate projects 0\u201310), and Checklist (evaluate based on a set of predefined criteria).");
        en.put("faq.q4", "How do I create a competition?");
        en.put("faq.a4", "Go to your Admin Dashboard and click 'Create Competition'. Fill in the name, dates, event type, and configure categories and judges as needed.");
        en.put("faq.q5", "How do I submit a project?");
        en.put("faq.a5", "Navigate to a competition and click 'Submit Project'. Fill in the project name, description, select the category, and optionally invite collaborators.");
        en.put("faq.q6", "What are categories?");
        en.put("faq.a6", "Categories are groupings within a competition. Each category can have its own voting type and checklist items. Projects are submitted to specific categories.");
        en.put("faq.q7", "How is the ranking calculated?");
        en.put("faq.a7", "Rankings are calculated based on the total votes or score received per project within a category. There is a separate Judges' Ranking (based on judge votes) and a Popular Ranking (based on public votes).");
        en.put("faq.q8", "What are invitations?");
        en.put("faq.a8", "Invitations allow project owners to invite collaborators to join their project team. Invited users receive a notification and can accept or decline the invitation.");
        en.put("faq.q9", "Can I manage multiple projects?");
        en.put("faq.a9", "Yes! You can submit projects to multiple competitions and categories. All your projects are visible under 'My Projects' in the navigation menu.");
        en.put("faq.q10", "How do notifications work?");
        en.put("faq.a10", "Notifications are sent automatically for key events such as competition activations, project acceptances or declines, and new invitations. You can view all notifications from the Notifications page.");
        en.put("faq.q11", "Is Votify free to use?");
        en.put("faq.a11", "Yes, Votify is an academic open-source project distributed under the MIT License. It is free to use, study, and adapt for educational or research purposes.");
        en.put("faq.q12", "How do I edit my profile?");
        en.put("faq.a12", "Click on your profile avatar or navigate to 'Edit Profile' from the navigation menu. You can update your name, email, profile picture, and password from there.");

        // --- Manage Competition View ---
        en.put("manage.accessdenied", "Access denied.");
        en.put("manage.notfound", "Competition not found");
        en.put("manage.back", "Back");
        en.put("manage.nodescription", "No description available.");
        en.put("manage.votingstatus", "Voting Status: ");
        en.put("manage.votingwindow", "Voting Window Settings");
        en.put("manage.startdatetime", "Start Date & Time");
        en.put("manage.enddatetime", "End Date & Time");
        en.put("manage.save", "Save");
        en.put("manage.savetooltip", "Save changes to the voting window dates");
        en.put("manage.activate", "Activate Competition");
        en.put("manage.activatetooltip", "Make the competition live and visible to participants");
        en.put("manage.votingoff", "Voting: OFF");
        en.put("manage.votingtooltip", "Toggle voting open or closed for participants");
        en.put("manage.pause", "Pause Competition");
        en.put("manage.pausetooltip", "Temporarily halt the competition, stopping all activity");
        en.put("manage.endvoting", "End Voting Now");
        en.put("manage.endvotingtooltip", "Immediately conclude the voting period");
        en.put("manage.reopenvoting", "Reopen Voting");
        en.put("manage.reopentooltip", "Start a new voting period for a concluded competition");
        en.put("manage.status.draft", "DRAFT");
        en.put("manage.status.votingopen", "VOTING OPEN");
        en.put("manage.votingon", "Voting: ON");
        en.put("manage.status.active", "ACTIVE");
        en.put("manage.resume", "Resume Competition");
        en.put("manage.status.paused", "PAUSED");
        en.put("manage.status.concluded", "CONCLUDED");
        en.put("manage.status.archived", "ARCHIVED");
        en.put("manage.enddatebeforestart", "End date cannot be before start date");
        en.put("manage.votingupdated", "Voting window updated successfully.");
        en.put("manage.activated", "Competition activated.");
        en.put("manage.votingclosed", "Voting closed.");
        en.put("manage.votingopened", "Voting opened.");
        en.put("manage.resumed", "Competition resumed.");
        en.put("manage.paused", "Competition paused.");
        en.put("manage.endvotingconfirm", "End Voting Now");
        en.put("manage.endvotingmsg", "Are you sure you want to end voting now? No more votes will be accepted until voting is reopened. You can reopen voting manually at any time.");
        en.put("manage.confirm", "Confirm");
        en.put("manage.votingended", "Voting has been ended.");
        en.put("manage.cancel", "Cancel");
        en.put("manage.reopenvotingconfirm", "Reopen Voting");
        en.put("manage.setnewend", "Set a new end date for the voting period:");
        en.put("manage.newenddatetime", "New End Date & Time");
        en.put("manage.validdate", "Please select a valid future date");
        en.put("manage.reopeneduntil", "Voting reopened until ");
        en.put("manage.pendingsubmissions", "Pending Project Submissions");
        en.put("manage.unknown", "Unknown");
        en.put("manage.submittedby", "Submitted by: ");
        en.put("manage.projectinvitation", "Project Invitation");
        en.put("manage.projectinvitation.body", "%s invited you to join \"%s\" in \"%s\".");
        en.put("manage.projectaccepted.title", "Project Accepted");
        en.put("manage.projectaccepted.body", "Your project \"%s\" has been accepted to \"%s\"!");
        en.put("manage.acceptedsuccess", "Project accepted.");
        en.put("manage.erroraccept", "Error accepting project");
        en.put("manage.projectdeclined.title", "Project Declined");
        en.put("manage.projectdeclined.body", "Your project \"%s\" has been declined for \"%s\".");
        en.put("manage.declinedsuccess", "Project declined and removed.");
        en.put("manage.errordecline", "Error declining project");

        // --- Notification View ---
        en.put("notifview.markallread", "Mark all as read");
        en.put("notifview.refreshlabel", "Refresh notifications");
        en.put("notifview.refresh", "Refresh");
        en.put("notifview.delete", "Delete");
        en.put("notifview.deleteall", "Delete all notifications");
        en.put("notifview.unread.plural", "unread notifications");
        en.put("notifview.unread.singular", "unread notification");
        en.put("notifview.empty", "You'll see your notifications here when you have any");

        // --- User Profile View extras ---
        en.put("profile.back", "Back");
        en.put("profile.helpfaq", "Help & FAQ");
        en.put("profile.confirmpassword", "Confirm your password");
        en.put("profile.enterpassword", "Enter your password");
        en.put("profile.incorrectpassword", "Incorrect password");

        // --- Project Details View extras ---
        en.put("project.details.aifeedback", "AI Feedback");
        en.put("project.details.aifeedback.tooltip", "Get AI-powered feedback and suggestions for your project");

        // --- User Projects View extras ---
        en.put("projects.unknown", "Unknown");

        // --- Create Project Dialog ---
        en.put("dialog.createproject.title", "Submit Project");
        en.put("dialog.createproject.name", "Project Name");
        en.put("dialog.createproject.name.placeholder", "Enter project name");
        en.put("dialog.createproject.description", "Description");
        en.put("dialog.createproject.description.placeholder", "Describe your project...");
        en.put("dialog.createproject.categories", "Categories");
        en.put("dialog.createproject.collaborators", "Invite Collaborators");
        en.put("dialog.createproject.invite.placeholder", "Enter username to invite");
        en.put("dialog.createproject.invite", "Invite");
        en.put("dialog.createproject.enterusername", "Please enter a username");
        en.put("dialog.createproject.usernotexist", "' does not exist");
        en.put("dialog.createproject.alreadyinvited", "This user is already invited");
        en.put("dialog.createproject.invitedsuccess", "' invited successfully");
        en.put("dialog.createproject.participants", "Added Participants (");
        en.put("dialog.createproject.delete", "Delete");
        en.put("dialog.createproject.removed", "Removed ");
        en.put("dialog.createproject.cancel", "Cancel");
        en.put("dialog.createproject.submit", "Submit");
        en.put("dialog.createproject.namerequired", "Project name is required");
        en.put("dialog.createproject.namelength", "Project name must be between 6 and 20 characters");
        en.put("dialog.createproject.mustsignin", "You must be signed in to submit a project");
        en.put("dialog.createproject.selectcategory", "Select at least one category");
        en.put("dialog.createproject.notif.title", "New Project Submission");
        en.put("dialog.createproject.success", "Project submitted for review!");
        en.put("dialog.createproject.error", "Error submitting project: ");

        // --- Checklist Voting Dialog ---
        en.put("dialog.checklist.title", "Checklist Voting: ");
        en.put("dialog.checklist.instructions", "Select the criteria that apply to this project:");
        en.put("dialog.checklist.confirm", "Confirm");
        en.put("dialog.checklist.cancel", "Cancel");
        en.put("dialog.checklist.selectone", "Please select at least one item");
        en.put("dialog.checklist.error", "Error submitting votes: ");

        // --- Invitation Dialog ---
        en.put("dialog.invitation.title", "Project Invitation");
        en.put("dialog.invitation.notfound", "Invitation not found.");
        en.put("dialog.invitation.project", "Project");
        en.put("dialog.invitation.invitedby", "Invited by");
        en.put("dialog.invitation.competition", "Competition");
        en.put("dialog.invitation.cancel", "Cancel");
        en.put("dialog.invitation.accept", "Accept");
        en.put("dialog.invitation.reject", "Reject");
        en.put("dialog.invitation.accepted", "Invitation accepted!");
        en.put("dialog.invitation.refused", "Invitation refused.");
        en.put("dialog.invitation.error", "Error: ");

        // --- Certificate Card Component ---
        en.put("card.certificate.category", "Category: ");
        en.put("card.certificate.issued", "Issued: ");
        en.put("card.certificate.download", "Download");
        en.put("card.certificate.viewdetails", "View Details");
        en.put("card.certificate.errorpdf", "Error opening PDF: ");

        // --- Competition Card Component extras ---
        en.put("card.competition.start", "Start: ");
        en.put("card.competition.end", "End: ");
        en.put("card.competition.active", "Active");
        en.put("card.competition.finished", "Finished");
        en.put("card.competition.paused", "Paused");
        en.put("card.competition.viewdetails", "View Details");

        // --- Category Card Component extras ---
        en.put("card.category.normal", "Normal");
        en.put("card.category.scale", "Scale");
        en.put("card.category.checklist", "Checklist");
        en.put("card.category.competition", "Competition: ");
        en.put("card.category.open", "OPEN");
        en.put("card.category.finished", "FINISHED");
        en.put("card.category.closed", "CLOSED");
        en.put("card.category.viewdetails", "View Details");

        // --- Notification Card Component extras ---
        en.put("card.notification.expires", "Expires: ");
        en.put("card.notification.viewproject", "View Project");
        en.put("card.notification.viewdetails", "View Details");
        en.put("card.notification.viewcerts", "View Certificates");
        en.put("card.notification.generatecerts", "YES - Generate Certificates");
        en.put("card.notification.cancel", "Cancel");
        en.put("card.notification.certsgenerated", "Certificates generated successfully for ");
        en.put("card.notification.certserror", "Error generating certificates: ");
        en.put("card.notification.justnow", "Just now");
        en.put("card.notification.minute.ago", " minute ago");
        en.put("card.notification.minutes.ago", " minutes ago");
        en.put("card.notification.hour.ago", " hour ago");
        en.put("card.notification.hours.ago", " hours ago");
        en.put("card.notification.day.ago", " day ago");
        en.put("card.notification.days.ago", " days ago");
        en.put("card.notification.expired", "Expired");
        en.put("card.notification.inmoments", "in moments");
        en.put("card.notification.in", "in ");
        en.put("card.notification.minute", " minute");
        en.put("card.notification.minutes", " minutes");
        en.put("card.notification.hour", " hour");
        en.put("card.notification.hours", " hours");
        en.put("card.notification.day", " day");
        en.put("card.notification.days", " days");

        // --- Podium Card Component extras ---
        en.put("card.podium.avgscore", "Avg. Score:");
        en.put("card.podium.score", "Score:");
        en.put("card.podium.totalchecks", "Total Checks:");
        en.put("card.podium.checks", "Checks:");
        en.put("card.podium.totalvotes", "Total Votes:");
        en.put("card.podium.votes", "Votes:");

        // --- Project Card Component extras ---
        en.put("card.project.nodescription", "No description provided");
        en.put("card.project.viewdetails", "View Details");
        en.put("card.project.competition", "Competition: ");
        en.put("card.project.vote.singular", " vote");
        en.put("card.project.vote.plural", " votes");

        // --- Sentiment Donut Chart extras ---
        en.put("chart.sentiment.positive", "Positive");
        en.put("chart.sentiment.neutral", "Neutral");
        en.put("chart.sentiment.negative", "Negative");

        // --- Breadcrumb extras ---
        en.put("breadcrumb.home", "Home");

        translations.put(ENGLISH, en);

        Map<String, String> es = new HashMap<>();
        es.put("app.title", "Votify");

        es.put("nav.signin", "Iniciar Sesión");
        es.put("nav.register", "Registrarse");
        es.put("nav.signout", "Cerrar Sesión");
        es.put("nav.myprojects", "Mis Proyectos");
        es.put("nav.mycompetitions", "Mis Competiciones");
        es.put("nav.invitations", "Invitaciones");
        es.put("nav.editprofile", "Editar Perfil");
        es.put("nav.notifications", "Notificaciones");
        es.put("nav.viewallnotifications", "Ver Todas las Notificaciones");
        es.put("nav.nonotifications", "No hay notificaciones recientes");
        es.put("nav.errorloadingnotifications", "Error al cargar notificaciones");

        es.put("home.discover", "Descubrir Competiciones");
        es.put("home.findvote", "Encuentra y vota por los mejores proyectos");
        es.put("home.filter.all", "Todas");
        es.put("home.filter.active", "Activas");
        es.put("home.filter.finished", "Finalizadas");
        es.put("home.search.placeholder", "Buscar competiciones...");
        es.put("home.nocompetitions", "No se encontraron competiciones");
        es.put("home.nomatching", "No hay competiciones que coincidan con tu criterio.");
        es.put("home.errorloading", "Error al cargar competiciones: ");

        es.put("login.welcome", "Bienvenido de nuevo");
        es.put("login.signincontinue", "Inicia sesión para continuar");
        es.put("login.username", "Usuario");
        es.put("login.username.placeholder", "Ingresa tu usuario");
        es.put("login.password", "Contraseña");
        es.put("login.password.placeholder", "Ingresa tu contraseña");
        es.put("login.signin", "Iniciar Sesión");
        es.put("login.noaccount", "¿No tienes cuenta? Regístrate");
        es.put("login.fillall", "Por favor completa todos los campos.");
        es.put("login.success", "¡Inicio de sesión exitoso!");
        es.put("login.unexpectederror", "Ocurrió un error inesperado.");

        es.put("register.title", "Crea tu cuenta");
        es.put("register.join", "Únete a Votify");
        es.put("register.createaccount", "Crea tu cuenta");
        es.put("register.username", "Usuario *");
        es.put("register.username.placeholder", "Elige un nombre de usuario");
        es.put("register.fullname", "Nombre Completo *");
        es.put("register.fullname.placeholder", "Ingresa tu nombre completo");
        es.put("register.email", "Correo Electrónico *");
        es.put("register.email.placeholder", "Ingresa tu correo electrónico");
        es.put("register.birthdate", "Fecha de Nacimiento *");
        es.put("register.password", "Contraseña *");
        es.put("register.password.placeholder", "Crea una contraseña");
        es.put("register.confirmpassword", "Confirmar Contraseña *");
        es.put("register.confirmpassword.placeholder", "Confirma tu contraseña");
        es.put("register.createbutton", "Crear Cuenta");
        es.put("register.fillall", "Por favor completa todos los campos requeridos.");
        es.put("register.passwordmismatch", "Las contraseñas no coinciden.");
        es.put("register.confirmtitle", "Confirmar Registro");
        es.put("register.confirmmessage", "¿Deseas crear tu cuenta con el usuario \"");
        es.put("register.yes", "Sí");
        es.put("register.no", "No");
        es.put("register.success", "¡Cuenta creada exitosamente!");
        es.put("register.alreadyaccount", "¿Ya tienes cuenta? Inicia sesión");
        es.put("register.profilepicture", "Foto de perfil (opcional)");
        es.put("register.dragphoto", "Arrastra tu foto de perfil aquí");
        es.put("register.imageerror", "Error al procesar la imagen.");
        es.put("register.welcome", "BIENVENIDO");
        es.put("register.ready", "Tu cuenta está lista — que comience la votación");

        es.put("profile.edityprofile", "Editar Perfil");
        es.put("profile.signout", "Cerrar Sesión");
        es.put("profile.sessionclosed", "Sesión cerrada");
        es.put("profile.deleteaccount", "Eliminar Cuenta");
        es.put("profile.mustsignin", "Debes iniciar sesión");
        es.put("profile.deleteconfirmation", "¿Estás seguro de que quieres eliminar tu cuenta? Esta acción no se puede deshacer.");
        es.put("profile.cancel", "Cancelar");
        es.put("profile.delete", "Eliminar");
        es.put("profile.accountdeletedsuccess", "Cuenta eliminada exitosamente");
        es.put("profile.errordeletingaccount", "Error al eliminar cuenta");
        es.put("profile.profileupdated", "Perfil actualizado");
        es.put("profile.errorupdatingprofile", "Error al actualizar perfil");

        es.put("projects.myprojects", "Mis Proyectos");
        es.put("projects.noprojectsyet", "Aún no tienes proyectos");
        es.put("projects.submitappear", "Los proyectos que envíes aparecerán aquí.");
        es.put("projects.accessdenied", "Acceso Denegado");
        es.put("projects.onlyviewown", "Solo puedes ver tus propios proyectos.");
        es.put("projects.errorloading", "Error al cargar proyectos: ");

        es.put("voting.title", "VOTACIÓN");
        es.put("voting.category", "Categoría: ");
        es.put("voting.no votes remaining", "No quedan votos");
        es.put("voting.youhave", "Tienes ");
        es.put("voting.votesleft", " voto");
        es.put("voting.votesleft.plural", " votos restantes");
        es.put("voting.markchecklist", "Marca los elementos de la lista para cada proyecto");
        es.put("voting.totalvotes", "Votos totales: ");
        es.put("voting.votepoints", "Votar (Lista)");
        es.put("voting.points", "Puntos");
        es.put("voting.vote", "Votar");
        es.put("voting.comments", "Comentarios");
        es.put("voting.commentsfor", "Comentarios de: ");
        es.put("voting.yourcomment", "Tu comentario");
        es.put("voting.writefeedback", "Escribe tu opinión aquí...");
        es.put("voting.save", "Guardar");
        es.put("voting.cancel", "Cancelar");
        es.put("voting.leavefeedback", "Deja tu opinión para este proyecto.");
        es.put("voting.commentempty", "El comentario no puede estar vacío.");
        es.put("voting.errorsavingcomment", "Error al guardar comentario: ");
        es.put("voting.mustlogin", "Debes iniciar sesión para votar.");
        es.put("voting.selectcategory", "Por favor selecciona una categoría antes de votar.");
        es.put("voting.assignpoints", "Debes asignar al menos 1 punto para votar.");
        es.put("voting.nocompetition", "Esta competición no acepta votos en este momento.");
        es.put("voting.onlyvotes", "¡Error! Solo tienes ");
        es.put("voting.votesavailable", " votos disponibles. No se pueden asignar ");
        es.put("voting.points.plural", " puntos.");
        es.put("voting.nochecklist", "No hay elementos de lista disponibles para esta categoría.");
        es.put("voting.checklisterror", "Error al abrir la votación por lista: ");
        es.put("voting.usernotfound", "Usuario no encontrado. Por favor inicia sesión de nuevo.");
        es.put("voting.nocompetitionvotes", "Esta competición no acepta votos en este momento.");
        es.put("voting.categorynotbelong", "La categoría no pertenece a esta competición.");

        es.put("ranking.title", "RANKING");
        es.put("ranking.categories", "Categorías");
        es.put("ranking.vote", "Votar");
        es.put("ranking.modifyentries", "Modificar entradas");
        es.put("ranking.registervoter", "Registrarse como Votante");
        es.put("ranking.notregistered", "No estás registrado como votante en esta competición. ¿Te gustaría registrarte como votante para participar en la votación?");
        es.put("ranking.yesregister", "Sí, regístrame");
        es.put("ranking.nostayhere", "No, quedarme aquí");
        es.put("ranking.voterregistered", "VOTANTE REGISTRADO");
        es.put("ranking.makevoiceheard", "Bienvenido — es hora de hacer escuchar tu voz");
        es.put("ranking.error", "Error: ");
        es.put("ranking.unexpectederror", "Error inesperado: ");
        es.put("ranking.start", "Inicio: ");
        es.put("ranking.end", "Fin: ");
        es.put("ranking.judgesranking", "Ranking de Jueces");
        es.put("ranking.popularranking", "Ranking Popular");
        es.put("ranking.noprojectscategory", "No hay proyectos en esta categoría");
        es.put("ranking.reclassify", "Reclasificar");
        es.put("ranking.declassify", "Desclasificar");
        es.put("ranking.editvotes", "Editar Votos");
        es.put("ranking.newposition", "Nueva posición");
        es.put("ranking.accept", "Aceptar");
        es.put("ranking.projectreclassified", "Proyecto reclasificado a la posición ");
        es.put("ranking.confirmdeclassify", "Desclasificar: ");
        es.put("ranking.confirmmsg", "¿Estás seguro de que quieres eliminar este proyecto de la competición? Esta acción no se puede deshacer. Todos los votos y comentarios se eliminarán permanentemente.");
        es.put("ranking.deletepermanently", "Eliminar permanentemente");
        es.put("ranking.projectdeclassified", "Proyecto desclasificado exitosamente");
        es.put("ranking.newamountofvotes", "Ingresa la nueva cantidad de votos:");
        es.put("ranking.votes", "Votos");
        es.put("ranking.votesupdated", "Votos actualizados a ");
        es.put("ranking.calculating", "Calculando rankings...");
        es.put("ranking.reclassifyaction", "Reclasificar: ");

        es.put("notification.title", "Notificaciones");
        es.put("notification.viewall", "Ver Todas");

        es.put("language.select", "Idioma");
        es.put("language.english", "Inglés");
        es.put("language.spanish", "Español");

        es.put("competition.details", "Detalles de la Competición");
        es.put("competition.status", "Estado");
        es.put("competition.categories", "Categorías");
        es.put("competition.create", "Crear Competición");
        es.put("competition.manage", "Gestionar");
        es.put("competition.configure", "Configurar");
        es.put("competition.createdby", "Creado por");

        es.put("projects.details.title", "Discusión del Proyecto");
        es.put("projects.details.nocomments", "Aún no hay comentarios");
        es.put("projects.details.startdiscussion", "Inicia la discusión dejando un comentario.");

        es.put("invitations.title", "Invitaciones");
        es.put("invitations.accept", "Aceptar");
        es.put("invitations.decline", "Rechazar");
        es.put("invitations.pending", "Pendiente");
        es.put("invitations.accepted", "Aceptada");
        es.put("invitations.declined", "Rechazada");
        es.put("invitations.invitedby", "Invitado por");
        es.put("invitations.acceptedmsg", "Invitación aceptada!");
        es.put("invitations.declinedmsg", "Invitación rechazada.");

        es.put("common.back", "Volver");
        es.put("common.error", "Error");
        es.put("common.success", "Éxito");
        es.put("common.loading", "Cargando...");
        es.put("common.na", "N/D");

        es.put("card.view", "VER");
        es.put("card.viewcategory", "VER CATEGORÍA");

        // --- Admin Dashboard ---
        es.put("admin.createcompetition", "Crear Competición");
        es.put("admin.nocompetitions", "Aún no hay competiciones");
        es.put("admin.createfirst", "¡Crea tu primera competición para empezar!");
        es.put("admin.status.active", "ACTIVA");
        es.put("admin.status.finished", "FINALIZADA");
        es.put("admin.status.paused", "PAUSADA");
        es.put("admin.eventtype", "Tipo de Evento: ");
        es.put("admin.description", "Descripción: ");
        es.put("admin.nodescription", "Sin descripción");
        es.put("admin.noenddate", "Sin fecha de fin");
        es.put("admin.enddate", "Fecha de Fin: ");
        es.put("admin.configure", "Configurar Competición");
        es.put("admin.manage", "Gestionar Competición");
        es.put("admin.accessdenied", "Acceso denegado. Solo puedes ver tu propio panel de administración.");

        // --- AI Feedback View ---
        es.put("aifeedback.back", "Volver");
        es.put("aifeedback.competition", "Competición");
        es.put("aifeedback.project", "Proyecto");
        es.put("aifeedback.generate", "Generar Retroalimentación");
        es.put("aifeedback.lastgenerated.empty", "Último generado: --/--/----");
        es.put("aifeedback.projects", "Proyectos");
        es.put("aifeedback.overview", "Resumen");
        es.put("aifeedback.nofeedback", "Sin retroalimentación generada");
        es.put("aifeedback.selectproject.hint", "Selecciona un proyecto y haz clic en \"Generar Retroalimentación\" para analizar comentarios con IA.");
        es.put("aifeedback.noprojects", "No tienes proyectos asignados.");
        es.put("aifeedback.selectfirst", "Por favor, selecciona un proyecto primero.");
        es.put("aifeedback.generating", "Generando...");
        es.put("aifeedback.success", "Retroalimentación generada exitosamente.");
        es.put("aifeedback.ratelimit", "Límite de solicitudes de IA alcanzado. Por favor, espera un momento e inténtalo de nuevo.");
        es.put("aifeedback.error", "Error al generar retroalimentación: ");
        es.put("aifeedback.unavailable", "El análisis de IA no pudo completarse. El servicio puede estar temporalmente no disponible. Por favor, inténtalo más tarde.");
        es.put("aifeedback.section", "Análisis de IA");
        es.put("aifeedback.positive", "Aspectos Positivos");
        es.put("aifeedback.negative", "Aspectos Negativos");
        es.put("aifeedback.sentiment", "Sentimiento General");
        es.put("aifeedback.analyzed", "Comentarios Analizados");
        es.put("aifeedback.positivecomments", "Comentarios Positivos");
        es.put("aifeedback.neutralcomments", "Comentarios Neutrales");
        es.put("aifeedback.negativecomments", "Comentarios Negativos");
        es.put("aifeedback.distribution", "Distribución de Sentimientos");
        es.put("aifeedback.frequentwords", "Palabras Frecuentes");
        es.put("aifeedback.nowords.unavailable", "El análisis de IA no pudo completarse. No hay palabras frecuentes disponibles.");
        es.put("aifeedback.nowords", "No se identificaron palabras frecuentes significativas en los comentarios analizados.");
        es.put("aifeedback.lastgenerated", "Último generado: ");
        es.put("aifeedback.nopoints", "No se identificaron puntos.");

        // --- Category Selection View ---
        es.put("catselection.notfound", "Competición no encontrada.");
        es.put("catselection.removed", "Puede que haya sido eliminada o el enlace no sea válido.");
        es.put("catselection.backhome", "← Volver al inicio");
        es.put("catselection.submit", "Enviar Proyecto");
        es.put("catselection.signin", "Inicia sesión para enviar un proyecto");
        es.put("catselection.start", "Inicio: ");
        es.put("catselection.end", "Fin: ");
        es.put("catselection.categories.count", " categorías");
        es.put("catselection.status.active", "ACTIVA");
        es.put("catselection.status.finished", "FINALIZADA");
        es.put("catselection.status.paused", "PAUSADA");
        es.put("catselection.search", "Buscar categoría...");
        es.put("catselection.nocategories", "No se encontraron categorías");
        es.put("catselection.nomatching", "No hay categorías que coincidan con tu búsqueda.");
        es.put("catselection.loading", "Cargando categorías...");

        // --- Certificates View ---
        es.put("cert.type", "Tipo de Certificado");
        es.put("cert.all", "Todos");
        es.put("cert.participant", "Participante");
        es.put("cert.winner", "Ganador");
        es.put("cert.competition", "Competición");
        es.put("cert.search", "Buscar");
        es.put("cert.search.placeholder", "Buscar por competición o proyecto...");
        es.put("cert.refresh.label", "Actualizar certificados");
        es.put("cert.refresh.title", "Actualizar");
        es.put("cert.erroruser", "No se pudo cargar el usuario actual");
        es.put("cert.loaded", "Certificados cargados exitosamente");
        es.put("cert.errorloading", "Error al cargar certificados: ");
        es.put("cert.none", "No se encontraron certificados");

        // --- Competition View ---
        es.put("compview.category", "Categoría:");
        es.put("compview.selectcategory", "Seleccionar una categoría");
        es.put("compview.general", "General");
        es.put("compview.vote", "Votar por Proyectos");
        es.put("compview.votetooltip", "Ir a la página de votación de esta competición");
        es.put("compview.position4", "Posición 4");
        es.put("compview.totalchecks", "Verificaciones Totales: ");
        es.put("compview.avgscore", "Punt. Media: %.1f/10");
        es.put("compview.totalvotes", "Votos Totales: ");

        // --- Configure Competition View ---
        es.put("configure.accessdenied", "Acceso denegado. Solo el creador de la competición puede configurarla.");
        es.put("configure.invalidid", "ID de competición inválido.");
        es.put("configure.general", "GENERAL");
        es.put("configure.startdate", "FECHA DE INICIO");
        es.put("configure.starttime", "HORA DE INICIO");
        es.put("configure.enddate", "FECHA DE FIN");
        es.put("configure.endtime", "HORA DE FIN");
        es.put("configure.coverimage", "IMAGEN DE PORTADA");
        es.put("configure.currentcover", "Portada actual");
        es.put("configure.coverpreview", "Vista previa de portada");
        es.put("configure.errorimage", "Error al leer la imagen");
        es.put("configure.categories", "CATEGORÍAS");
        es.put("configure.addcategory", "Añadir Categoría");
        es.put("configure.type.normal", "Normal");
        es.put("configure.type.scale", "Escala");
        es.put("configure.type.checklist", "Lista de Verificación");
        es.put("configure.deletecategory", "Eliminar categoría");
        es.put("configure.categoryremoved", "Categoría eliminada");
        es.put("configure.addnewcategory", "Añadir Nueva Categoría");
        es.put("configure.categoryname", "Nombre de Categoría");
        es.put("configure.votingtype", "Tipo de Votación");
        es.put("configure.save", "Guardar");
        es.put("configure.categoryrequired", "El nombre de la categoría es obligatorio");
        es.put("configure.categoryadded", "Categoría añadida (pendiente de guardar)");
        es.put("configure.cancel", "Cancelar");
        es.put("configure.participation", "PARTICIPACIÓN");
        es.put("configure.whocanvote", "QUIÉN PUEDE VOTAR");
        es.put("configure.option.judges", "Jueces");
        es.put("configure.option.everyone", "Todos");
        es.put("configure.autovote", "VOTO AUTOMÁTICO");
        es.put("configure.option.off", "DESACTIVADO");
        es.put("configure.option.on", "ACTIVADO");
        es.put("configure.votesperperson", "VOTOS POR PERSONA");
        es.put("configure.addjudges", "AÑADIR JUECES");
        es.put("configure.addjudge", "Añadir Juez");
        es.put("configure.removejudge", "Eliminar juez");
        es.put("configure.addnewjudge", "Añadir Nuevo Juez");
        es.put("configure.judgeusername", "Usuario del Juez");
        es.put("configure.enterusername", "Introduce el nombre de usuario");
        es.put("configure.judgerequired", "Por favor introduce el nombre de usuario del juez");
        es.put("configure.usernotfound", "Usuario no encontrado: ");
        es.put("configure.alreadyjudge", "Este usuario ya es juez en esta competición");
        es.put("configure.judgepending", "Juez pendiente de guardar");
        es.put("configure.alreadyadded", "Este usuario ya ha sido añadido");
        es.put("configure.pending", "(Pendiente)");
        es.put("configure.removependingjudge", "Eliminar juez pendiente");
        es.put("configure.votetype", "TIPO DE VOTO");
        es.put("configure.votingmode", "MODO DE VOTACIÓN");
        es.put("configure.mode.normal", "Normal");
        es.put("configure.mode.checklist", "Lista de Verificación");
        es.put("configure.mode.scale", "Escala (0-10)");
        es.put("configure.checklistitems", "ELEMENTOS DE LISTA");
        es.put("configure.addchecklistitem", "Añadir Elemento");
        es.put("configure.deletechecklistitem", "Eliminar elemento de lista");
        es.put("configure.addnewchecklistitem", "Añadir Nuevo Elemento");
        es.put("configure.itemdescription", "Descripción del Elemento");
        es.put("configure.itemrequired", "La descripción del elemento es obligatoria");
        es.put("configure.scaleconfig", "CONFIGURACIÓN DE ESCALA");
        es.put("configure.scalefixed", "El rango de escala es fijo: 0 - 10");
        es.put("configure.comments", "COMENTARIOS");
        es.put("configure.allowcomments", "PERMITIR COMENTARIOS");
        es.put("configure.option.yes", "SÍ");
        es.put("configure.option.no", "NO");
        es.put("configure.requiredcomments", "COMENTARIOS OBLIGATORIOS");
        es.put("configure.confirmexit", "Confirmar Salida");
        es.put("configure.exitmsg", "¿Estás seguro de que quieres salir sin guardar los cambios?");
        es.put("configure.exitwithout", "Salir Sin Guardar");
        es.put("configure.continueediting", "Continuar Editando");
        es.put("configure.minvotes", "Error: El máximo de votos por persona debe ser al menos 1.");
        es.put("configure.errordeletecategories", "Error al eliminar categorías: ");
        es.put("configure.errorprocesscategories", "Error al procesar cambios de categoría: ");
        es.put("configure.errorprocessjudges", "Error al procesar cambios de jueces: ");
        es.put("configure.saved", "Configuración guardada exitosamente");
        es.put("configure.errorsave", "Error al guardar la configuración: ");

        // --- Create Competition View ---
        es.put("createcomp.accessdenied", "Acceso denegado. Solo puedes crear competiciones para tu propia cuenta.");
        es.put("createcomp.name", "Nombre de la Competición *");
        es.put("createcomp.name.placeholder", "Introduce el nombre (máx. 20 caracteres)");
        es.put("createcomp.description", "Descripción");
        es.put("createcomp.description.placeholder", "Introduce la descripción de la competición");
        es.put("createcomp.eventtype", "Tipo de Evento *");
        es.put("createcomp.type.tech", "Tecnología");
        es.put("createcomp.type.art", "Arte");
        es.put("createcomp.type.music", "Música");
        es.put("createcomp.type.sports", "Deportes");
        es.put("createcomp.type.business", "Negocios");
        es.put("createcomp.type.education", "Educación");
        es.put("createcomp.type.other", "Otro");
        es.put("createcomp.startdate", "Fecha de Inicio *");
        es.put("createcomp.enddate", "Fecha de Fin *");
        es.put("createcomp.coverimage", "Imagen de Portada (Opcional)");
        es.put("createcomp.coverpreview", "Vista previa de portada");
        es.put("createcomp.errorimage", "Error al leer la imagen");
        es.put("createcomp.categories", "Categorías");
        es.put("createcomp.categoryname", "Nombre de Categoría");
        es.put("createcomp.categoryname.placeholder", "Ej.: Juegos, Diseño, etc.");
        es.put("createcomp.votingtype", "Tipo de Votación");
        es.put("createcomp.type.normal", "Normal");
        es.put("createcomp.type.scale", "Escala");
        es.put("createcomp.type.checklist", "Lista de Verificación");
        es.put("createcomp.errorcatimage", "Error al leer la imagen de categoría");
        es.put("createcomp.addcategory", "Añadir Categoría");
        es.put("createcomp.zerocategories", "0 categorías añadidas");
        es.put("createcomp.judges", "Jueces");
        es.put("createcomp.judgeusername", "Usuario del Juez");
        es.put("createcomp.judgeusername.placeholder", "Introduce el nombre de usuario del juez");
        es.put("createcomp.addjudge", "Añadir Juez");
        es.put("createcomp.cancel", "Cancelar");
        es.put("createcomp.create", "Crear");
        es.put("createcomp.catnamerequired", "Por favor introduce el nombre de la categoría.");
        es.put("createcomp.catalready", "Categoría ya añadida.");
        es.put("createcomp.categories.count.singular", " categoría añadida");
        es.put("createcomp.categories.count.plural", " categorías añadidas");
        es.put("createcomp.judgerequired", "Por favor introduce el nombre de usuario del juez.");
        es.put("createcomp.judgenotfound", "Usuario juez no encontrado: ");
        es.put("createcomp.judgealready", "Juez ya añadido.");
        es.put("createcomp.mustlogin", "Debes iniciar sesión para crear una competición.");
        es.put("createcomp.success", "¡Competición creada exitosamente!");
        es.put("createcomp.error", "Ocurrió un error al crear la competición.");

        // --- FAQ View ---
        es.put("faq.q1", "¿Qué es Votify?");
        es.put("faq.a1", "Votify es una plataforma web académica para gestionar competiciones y evaluar proyectos. Admite múltiples modalidades de votación (clásica, escala y lista de verificación), retroalimentación asistida por IA y generación dinámica de certificados.");
        es.put("faq.q2", "¿Cómo funciona la votación?");
        es.put("faq.a2", "Una vez registrado como votante, puedes explorar las competiciones activas, seleccionar una categoría y emitir tus votos sobre los proyectos disponibles. Cada competición puede tener un número limitado de votos por participante.");
        es.put("faq.q3", "¿Cuáles son los diferentes tipos de voto?");
        es.put("faq.a3", "Votify admite tres tipos de voto: Normal (voto de popularidad clásico), Escala (puntúa proyectos del 0 al 10) y Lista de Verificación (evalúa según un conjunto de criterios predefinidos).");
        es.put("faq.q4", "¿Cómo creo una competición?");
        es.put("faq.a4", "Ve a tu Panel de Administración y haz clic en 'Crear Competición'. Rellena el nombre, las fechas, el tipo de evento y configura las categorías y los jueces según sea necesario.");
        es.put("faq.q5", "¿Cómo envío un proyecto?");
        es.put("faq.a5", "Navega a una competición y haz clic en 'Enviar Proyecto'. Rellena el nombre del proyecto, la descripción, selecciona la categoría e invita colaboradores opcionalmente.");
        es.put("faq.q6", "¿Qué son las categorías?");
        es.put("faq.a6", "Las categorías son agrupaciones dentro de una competición. Cada categoría puede tener su propio tipo de votación y elementos de lista de verificación. Los proyectos se envían a categorías específicas.");
        es.put("faq.q7", "¿Cómo se calcula el ranking?");
        es.put("faq.a7", "Los rankings se calculan según el total de votos o puntuación recibida por cada proyecto dentro de una categoría. Existe un Ranking de Jueces (basado en votos de jueces) y un Ranking Popular (basado en votos públicos) por separado.");
        es.put("faq.q8", "¿Qué son las invitaciones?");
        es.put("faq.a8", "Las invitaciones permiten a los propietarios de proyectos invitar a colaboradores a unirse a su equipo. Los usuarios invitados reciben una notificación y pueden aceptar o rechazar la invitación.");
        es.put("faq.q9", "¿Puedo gestionar múltiples proyectos?");
        es.put("faq.a9", "¡Sí! Puedes enviar proyectos a múltiples competiciones y categorías. Todos tus proyectos son visibles en 'Mis Proyectos' en el menú de navegación.");
        es.put("faq.q10", "¿Cómo funcionan las notificaciones?");
        es.put("faq.a10", "Las notificaciones se envían automáticamente para eventos clave como activaciones de competiciones, aceptaciones o rechazos de proyectos e invitaciones nuevas. Puedes ver todas las notificaciones desde la página de Notificaciones.");
        es.put("faq.q11", "¿Es gratuito usar Votify?");
        es.put("faq.a11", "Sí, Votify es un proyecto académico de código abierto distribuido bajo la Licencia MIT. Es gratuito para usar, estudiar y adaptar con fines educativos o de investigación.");
        es.put("faq.q12", "¿Cómo edito mi perfil?");
        es.put("faq.a12", "Haz clic en tu avatar de perfil o navega a 'Editar Perfil' desde el menú de navegación. Desde allí puedes actualizar tu nombre, correo, foto de perfil y contraseña.");

        // --- Manage Competition View ---
        es.put("manage.accessdenied", "Acceso denegado.");
        es.put("manage.notfound", "Competición no encontrada");
        es.put("manage.back", "Volver");
        es.put("manage.nodescription", "Sin descripción disponible.");
        es.put("manage.votingstatus", "Estado de Votación: ");
        es.put("manage.votingwindow", "Configuración de Ventana de Votación");
        es.put("manage.startdatetime", "Fecha y Hora de Inicio");
        es.put("manage.enddatetime", "Fecha y Hora de Fin");
        es.put("manage.save", "Guardar");
        es.put("manage.savetooltip", "Guardar cambios en las fechas de la ventana de votación");
        es.put("manage.activate", "Activar Competición");
        es.put("manage.activatetooltip", "Hacer la competición visible y activa para los participantes");
        es.put("manage.votingoff", "Votación: DESACTIVADA");
        es.put("manage.votingtooltip", "Activar o desactivar la votación para los participantes");
        es.put("manage.pause", "Pausar Competición");
        es.put("manage.pausetooltip", "Pausar temporalmente la competición, deteniendo toda actividad");
        es.put("manage.endvoting", "Terminar Votación Ahora");
        es.put("manage.endvotingtooltip", "Concluir inmediatamente el período de votación");
        es.put("manage.reopenvoting", "Reabrir Votación");
        es.put("manage.reopentooltip", "Iniciar un nuevo período de votación para una competición concluida");
        es.put("manage.status.draft", "BORRADOR");
        es.put("manage.status.votingopen", "VOTACIÓN ABIERTA");
        es.put("manage.votingon", "Votación: ACTIVADA");
        es.put("manage.status.active", "ACTIVA");
        es.put("manage.resume", "Reanudar Competición");
        es.put("manage.status.paused", "PAUSADA");
        es.put("manage.status.concluded", "CONCLUIDA");
        es.put("manage.status.archived", "ARCHIVADA");
        es.put("manage.enddatebeforestart", "La fecha de fin no puede ser anterior a la de inicio");
        es.put("manage.votingupdated", "Ventana de votación actualizada exitosamente.");
        es.put("manage.activated", "Competición activada.");
        es.put("manage.votingclosed", "Votación cerrada.");
        es.put("manage.votingopened", "Votación abierta.");
        es.put("manage.resumed", "Competición reanudada.");
        es.put("manage.paused", "Competición pausada.");
        es.put("manage.endvotingconfirm", "Terminar Votación Ahora");
        es.put("manage.endvotingmsg", "¿Estás seguro de que quieres terminar la votación ahora? No se aceptarán más votos hasta que se reabra. Puedes reabrir la votación manualmente en cualquier momento.");
        es.put("manage.confirm", "Confirmar");
        es.put("manage.votingended", "La votación ha terminado.");
        es.put("manage.cancel", "Cancelar");
        es.put("manage.reopenvotingconfirm", "Reabrir Votación");
        es.put("manage.setnewend", "Establece una nueva fecha de fin para el período de votación:");
        es.put("manage.newenddatetime", "Nueva Fecha y Hora de Fin");
        es.put("manage.validdate", "Por favor selecciona una fecha futura válida");
        es.put("manage.reopeneduntil", "Votación reabierta hasta ");
        es.put("manage.pendingsubmissions", "Envíos de Proyectos Pendientes");
        es.put("manage.unknown", "Desconocido");
        es.put("manage.submittedby", "Enviado por: ");
        es.put("manage.projectinvitation", "Invitación de Proyecto");
        es.put("manage.projectinvitation.body", "%s te invitó a unirte a \"%s\" en \"%s\".");
        es.put("manage.projectaccepted.title", "Proyecto Aceptado");
        es.put("manage.projectaccepted.body", "¡Tu proyecto \"%s\" ha sido aceptado en \"%s\"!");
        es.put("manage.acceptedsuccess", "Proyecto aceptado.");
        es.put("manage.erroraccept", "Error al aceptar el proyecto");
        es.put("manage.projectdeclined.title", "Proyecto Rechazado");
        es.put("manage.projectdeclined.body", "Tu proyecto \"%s\" ha sido rechazado para \"%s\".");
        es.put("manage.declinedsuccess", "Proyecto rechazado y eliminado.");
        es.put("manage.errordecline", "Error al rechazar el proyecto");

        // --- Notification View ---
        es.put("notifview.markallread", "Marcar todo como leído");
        es.put("notifview.refreshlabel", "Actualizar notificaciones");
        es.put("notifview.refresh", "Actualizar");
        es.put("notifview.delete", "Eliminar");
        es.put("notifview.deleteall", "Eliminar todas las notificaciones");
        es.put("notifview.unread.plural", "notificaciones sin leer");
        es.put("notifview.unread.singular", "notificación sin leer");
        es.put("notifview.empty", "Verás tus notificaciones aquí cuando tengas alguna");

        // --- User Profile View extras ---
        es.put("profile.back", "Volver");
        es.put("profile.helpfaq", "Ayuda y FAQ");
        es.put("profile.confirmpassword", "Confirma tu contraseña");
        es.put("profile.enterpassword", "Introduce tu contraseña");
        es.put("profile.incorrectpassword", "Contraseña incorrecta");

        // --- Project Details View extras ---
        es.put("project.details.aifeedback", "Retroalimentación IA");
        es.put("project.details.aifeedback.tooltip", "Obtén retroalimentación y sugerencias basadas en IA para tu proyecto");

        // --- User Projects View extras ---
        es.put("projects.unknown", "Desconocido");

        // --- Create Project Dialog ---
        es.put("dialog.createproject.title", "Enviar Proyecto");
        es.put("dialog.createproject.name", "Nombre del Proyecto");
        es.put("dialog.createproject.name.placeholder", "Introduce el nombre del proyecto");
        es.put("dialog.createproject.description", "Descripción");
        es.put("dialog.createproject.description.placeholder", "Describe tu proyecto...");
        es.put("dialog.createproject.categories", "Categorías");
        es.put("dialog.createproject.collaborators", "Invitar Colaboradores");
        es.put("dialog.createproject.invite.placeholder", "Introduce el nombre de usuario a invitar");
        es.put("dialog.createproject.invite", "Invitar");
        es.put("dialog.createproject.enterusername", "Por favor introduce un nombre de usuario");
        es.put("dialog.createproject.usernotexist", "' no existe");
        es.put("dialog.createproject.alreadyinvited", "Este usuario ya está invitado");
        es.put("dialog.createproject.invitedsuccess", "' invitado exitosamente");
        es.put("dialog.createproject.participants", "Participantes Añadidos (");
        es.put("dialog.createproject.delete", "Eliminar");
        es.put("dialog.createproject.removed", "Eliminado ");
        es.put("dialog.createproject.cancel", "Cancelar");
        es.put("dialog.createproject.submit", "Enviar");
        es.put("dialog.createproject.namerequired", "El nombre del proyecto es obligatorio");
        es.put("dialog.createproject.namelength", "El nombre del proyecto debe tener entre 6 y 20 caracteres");
        es.put("dialog.createproject.mustsignin", "Debes iniciar sesión para enviar un proyecto");
        es.put("dialog.createproject.selectcategory", "Selecciona al menos una categoría");
        es.put("dialog.createproject.notif.title", "Nueva Solicitud de Proyecto");
        es.put("dialog.createproject.success", "¡Proyecto enviado para revisión!");
        es.put("dialog.createproject.error", "Error al enviar el proyecto: ");

        // --- Checklist Voting Dialog ---
        es.put("dialog.checklist.title", "Votación por Lista: ");
        es.put("dialog.checklist.instructions", "Selecciona los criterios que aplican a este proyecto:");
        es.put("dialog.checklist.confirm", "Confirmar");
        es.put("dialog.checklist.cancel", "Cancelar");
        es.put("dialog.checklist.selectone", "Por favor selecciona al menos un elemento");
        es.put("dialog.checklist.error", "Error al enviar los votos: ");

        // --- Invitation Dialog ---
        es.put("dialog.invitation.title", "Invitación de Proyecto");
        es.put("dialog.invitation.notfound", "Invitación no encontrada.");
        es.put("dialog.invitation.project", "Proyecto");
        es.put("dialog.invitation.invitedby", "Invitado por");
        es.put("dialog.invitation.competition", "Competición");
        es.put("dialog.invitation.cancel", "Cancelar");
        es.put("dialog.invitation.accept", "Aceptar");
        es.put("dialog.invitation.reject", "Rechazar");
        es.put("dialog.invitation.accepted", "¡Invitación aceptada!");
        es.put("dialog.invitation.refused", "Invitación rechazada.");
        es.put("dialog.invitation.error", "Error: ");

        // --- Certificate Card Component ---
        es.put("card.certificate.category", "Categoría: ");
        es.put("card.certificate.issued", "Emitido: ");
        es.put("card.certificate.download", "Descargar");
        es.put("card.certificate.viewdetails", "Ver Detalles");
        es.put("card.certificate.errorpdf", "Error al abrir el PDF: ");

        // --- Competition Card Component extras ---
        es.put("card.competition.start", "Inicio: ");
        es.put("card.competition.end", "Fin: ");
        es.put("card.competition.active", "Activa");
        es.put("card.competition.finished", "Finalizada");
        es.put("card.competition.paused", "Pausada");
        es.put("card.competition.viewdetails", "Ver Detalles");

        // --- Category Card Component extras ---
        es.put("card.category.normal", "Normal");
        es.put("card.category.scale", "Escala");
        es.put("card.category.checklist", "Lista de Verificación");
        es.put("card.category.competition", "Competición: ");
        es.put("card.category.open", "ABIERTA");
        es.put("card.category.finished", "FINALIZADA");
        es.put("card.category.closed", "CERRADA");
        es.put("card.category.viewdetails", "Ver Detalles");

        // --- Notification Card Component extras ---
        es.put("card.notification.expires", "Expira: ");
        es.put("card.notification.viewproject", "Ver Proyecto");
        es.put("card.notification.viewdetails", "Ver Detalles");
        es.put("card.notification.viewcerts", "Ver Certificados");
        es.put("card.notification.generatecerts", "SÍ - Generar Certificados");
        es.put("card.notification.cancel", "Cancelar");
        es.put("card.notification.certsgenerated", "Certificados generados exitosamente para ");
        es.put("card.notification.certserror", "Error al generar certificados: ");
        es.put("card.notification.justnow", "Ahora mismo");
        es.put("card.notification.minute.ago", " minuto");
        es.put("card.notification.minutes.ago", " minutos");
        es.put("card.notification.hour.ago", " hora");
        es.put("card.notification.hours.ago", " horas");
        es.put("card.notification.day.ago", " día");
        es.put("card.notification.days.ago", " días");
        es.put("card.notification.expired", "Expirado");
        es.put("card.notification.inmoments", "en breve");
        es.put("card.notification.in", "en ");
        es.put("card.notification.minute", " minuto");
        es.put("card.notification.minutes", " minutos");
        es.put("card.notification.hour", " hora");
        es.put("card.notification.hours", " horas");
        es.put("card.notification.day", " día");
        es.put("card.notification.days", " días");

        // --- Podium Card Component extras ---
        es.put("card.podium.avgscore", "Punt. Media:");
        es.put("card.podium.score", "Puntuación:");
        es.put("card.podium.totalchecks", "Verificaciones Totales:");
        es.put("card.podium.checks", "Verificaciones:");
        es.put("card.podium.totalvotes", "Votos Totales:");
        es.put("card.podium.votes", "Votos:");

        // --- Project Card Component extras ---
        es.put("card.project.nodescription", "Sin descripción disponible");
        es.put("card.project.viewdetails", "Ver Detalles");
        es.put("card.project.competition", "Competición: ");
        es.put("card.project.vote.singular", " voto");
        es.put("card.project.vote.plural", " votos");

        // --- Sentiment Donut Chart extras ---
        es.put("chart.sentiment.positive", "Positivo");
        es.put("chart.sentiment.neutral", "Neutral");
        es.put("chart.sentiment.negative", "Negativo");

        // --- Breadcrumb extras ---
        es.put("breadcrumb.home", "Inicio");

        translations.put(SPANISH, es);
    }

    public String getLocale() {
        VaadinSession session = VaadinSession.getCurrent();
        if (session != null) {
            String locale = (String) session.getAttribute(SESSION_KEY);
            return locale != null ? locale : ENGLISH;
        }
        return ENGLISH;
    }

    public void establecerIdioma(String localeCode) {
        if (ENGLISH.equals(localeCode) || SPANISH.equals(localeCode)) {
            VaadinSession session = VaadinSession.getCurrent();
            if (session != null) {
                session.setAttribute(SESSION_KEY, localeCode);
            }
        }
    }

    public String t(String key) {
        String locale = getLocale();
        Map<String, String> localeTranslations = translations.getOrDefault(locale, translations.get(ENGLISH));
        return localeTranslations.getOrDefault(key, key);
    }

    public String t(String key, String defaultValue) {
        String locale = getLocale();
        Map<String, String> localeTranslations = translations.getOrDefault(locale, translations.get(ENGLISH));
        return localeTranslations.getOrDefault(key, defaultValue);
    }
}