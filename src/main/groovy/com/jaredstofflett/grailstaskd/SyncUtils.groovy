package com.jaredstofflett.grailstaskd

import grails.converters.JSON
import org.grails.web.json.JSONElement

import javax.net.ssl.KeyManagerFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory
import java.security.KeyStore
import java.security.SecureRandom
import java.security.cert.Certificate
import java.text.SimpleDateFormat
import groovy.util.logging.Slf4j
@Slf4j
class SyncUtils {
// Following is a list of all attributes we can handle for tasks.
public static String[] jsonTags=["entry","modified","uuid","description","project","status","end","due","recur","imask","parent","mask","tags"]
    public static String getSyncString(TaskUser t, String taskJSON) {
        def trusted = KeyStore.getInstance(KeyStore.getDefaultType());
        trusted.load(null);
        def serverCert = CertsAndKeysUtils.getCert(t.caCert)
        def userCert = CertsAndKeysUtils.getCert(t.userCert)
        def chain = new Certificate[2];
        trusted.setCertificateEntry("taskwarrior-ROOT", serverCert)
        chain[1] = serverCert
        trusted.setCertificateEntry("taskwarrior-USER", userCert);
        chain[0] = userCert
        def keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        trusted.setEntry("user", new KeyStore.PrivateKeyEntry(CertsAndKeysUtils.loadPrivateKey(t.userKey), chain), new KeyStore.PasswordProtection("secret".toCharArray()));
        keyManagerFactory.init(trusted, "secret".toCharArray());
        def context = SSLContext.getInstance("TLS");
        def tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trusted);
        def trustManagers = tmf.getTrustManagers();
        context.init(keyManagerFactory.getKeyManagers(), trustManagers, new SecureRandom());
        def sslFact = context.getSocketFactory();
        def socket = sslFact.createSocket()
        socket.setUseClientMode(true)
        socket.setNeedClientAuth(true)
        socket.setTcpNoDelay(true)
        socket.connect(new InetSocketAddress(t.serverAddress, t.serverPort))
        socket.startHandshake()
        def scanner = null;
        def outStream = socket.getOutputStream()
        def inStream = socket.getInputStream()
        def dataStream = new DataOutputStream(outStream)
		// For information on taskd sync see https://taskwarrior.org/docs/design/request.html
        String taskString = new String("""XXXXkey: ${t.taskUserKey}
org: ${t.orgName}
user: ${t.taskUserName}
type: sync
client: taskd 1.0.0
protocol: v1
""")
        if (t.syncKey != null) {
            taskString += """
${t.syncKey}"""
        }

        if (taskJSON != null) {
            taskString += """
${taskJSON}
"""
        }
        taskString += """


"""
        if (taskJSON != null) {
        }

        byte[] dataBytes = taskString.getBytes("UTF-8")
        def dataLength = dataBytes.length
        dataBytes[0] = (byte) dataLength >>> 24
        dataBytes[1] = (byte) dataLength >>> 16
        dataBytes[2] = (byte) dataLength >>> 8
        dataBytes[3] = (byte) dataLength
        dataStream.write(dataBytes)
        dataStream.flush()
        scanner = new Scanner(inStream)
        scanner = scanner.useDelimiter("\\A");
        String results = scanner.next()
        scanner.close()
        socket.close()
        return results
    }

    public String getStringFromJSON(String s) {
        return s.substring(s.indexOf("{"), s.lastIndexOf("}") + 1)
    }

    public static SyncData getSyncInfo(def data) {
        ArrayList<JSONElement> tasks = new ArrayList<JSONElement>()
        int code = -1
        String status = null
        String syncKey = null
        data.eachLine {
            if (it.startsWith("{")) {
                tasks.add(JSON.parse(it))
            }
        }
        status = data.readLines().get(2).replace("status: ", "")
        code = data.readLines().get(1).replace("code: ", "").toInteger()
        syncKey = data.readLines().get(data.readLines().size() - 2)
        return new SyncData(syncKey: syncKey, code: code.toInteger(), tasks: tasks, status: status)
    }

    public static Date stringToDate(String s) {
        return s ? new SimpleDateFormat("yyyyMMdd'T'HHmmss").parse(s) : null
    }

    public static String dateToString(Date d) {
        return d.format("yyyyMMdd'T'HHmmss'Z'")
    }

		public static boolean addOrUpdateTask(def t) {
for (String s : t.keys()) { // If unknown attribute log an error and don't save this task
if (s!="user" &&!jsonTags.contains(s)) {
//With annotations on the task server we see a task with out the annotation and a task with the annotation
// Both have the same uid, so in case other attributes behave the same way
// if we find a task with an unknown attribute delete any previous versions of it from the database if they exist.
if (Task.findByUuidAndUser(t.uuid, t.user) != null) {
Task.findByUuidAndUser(t.uuid, t.user).delete(flush:true)
}
throw new TaskElementNotHandledException(s)
}
}
        def task = Task.findByUuidAndUser(t.uuid, t.user)
        t.entry = stringToDate(t.entry)
        t.modified = stringToDate(t.modified)
        t.end = stringToDate(t.end)
        t.due = stringToDate(t.due)
        if (task != null) {
t.tags.each {
task.addToTags(it)
}
t.tags=null // Already added tags so null the json
            task.properties = t
        } else {
            task = new Task(t)
//Do nothing with tags since if we hit this it's from entering the task on the UI which does not support tags.
        }
        return task.save(flush: true, failOnError: true)
    }

    public static boolean completeTask(Task t) {
        t.end = new Date()
        t.modified = new Date()
        t.status = "completed"
				// See following link for discussions of masks and imasks https://taskwarrior.org/docs/design/task.html
        if (t.imask != null) {
            def parentTask = Task.findByUuid(t.parent)
            def maskIndex = (int) t.imask.toDouble()
            def newMask = parentTask.mask.toCharArray()
            newMask[maskIndex] = "+"
            parentTask.mask = new String(newMask)
            parentTask.save(flush: true)
        }
        return t.save(flush: true)
    }

    public static String syncTasks(TaskUser u) {
        def tasks = Task.findAllByModifiedGreaterThanEqualsAndUser(u.syncDate, u)
        def converter
        String taskJSON = ""
        tasks.each {
            converter = it as JSON
            taskJSON = taskJSON + """${converter.toString()}
"""
        }
        if (taskJSON == "") {
            taskJSON = null
        }
        String data = SyncUtils.getSyncString(u, taskJSON)
        return data
    }

    public static void addUpdatedTasks(User u) {
        String data = """${getSyncString(u, null)}"""
        def syncInfo = getSyncInfo(data)
        u.syncKey = syncInfo.syncKey
        u.syncDate = new Date()
        u.save(flush: true)
        syncInfo.tasks.each {
            it.user = u
try {
            SyncUtils.addOrUpdateTask(it)
}
catch (TaskElementNotHandledException e) {
log.error(e.toString())
        }
    }
}
}