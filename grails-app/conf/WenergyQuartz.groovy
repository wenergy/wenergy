grails.plugin.quartz2.autoStartup = true

org{
    quartz{
        //anything here will get merged into the quartz.properties so you don't need another file
        scheduler.instanceName = 'wEnergyScheduler'
        threadPool.class = 'org.quartz.simpl.SimpleThreadPool'
        threadPool.threadCount = 20
        threadPool.threadsInheritContextClassLoaderOfInitializingThread = true
        jobStore.class = 'org.quartz.simpl.RAMJobStore'
    }
}
