package com.sarhanm.resolver

import groovy.mock.interceptor.MockFor
import org.gradle.api.artifacts.DependencyResolveDetails
import org.gradle.api.artifacts.ModuleVersionSelector
import org.junit.Test

/**
 *
 * @author mohammad sarhan
 */
class VersionResolveViaManifestTest {

    @Test
    void testManifestVersion() {
        def file = new File("src/test/resources/versions.yaml")

        def options = new VersionResolverOptions()
        options.versionManifest = file.toURI().toString()

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion(1) { params -> 'auto' }
        selectorMock.demand.getGroup { params -> 'com.coinfling' }
        selectorMock.demand.getName { params -> 'auth-service-api' }

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested(1) { params -> selectorMock.proxyInstance() }

        def details = detailsMock.proxyInstance()
        def resolver = new VersionResolver(null, options)
        def ver = resolver.resolveVersionFromManifest(details)
        assert ver == "1.0-SNAPSHOT"

    }

    @Test
    void testManifestVersionMissing()
    {
        def file = new File("src/test/resources/versions.yaml")

        def options = new VersionResolverOptions()
        options.versionManifest = file.toURI().toString()

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion{ params-> 'auto'}
        selectorMock.demand.getGroup{ params-> 'com.coinfling'}
        selectorMock.demand.getName{ params-> 'not-there'}

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested{params-> selectorMock.proxyInstance()}

        def resolver = new VersionResolver(null, options)
        def ver = resolver.resolveVersionFromManifest(detailsMock.proxyInstance())
        assert ver == "auto"
    }

    @Test
    void testNoExecution()
    {
        def file = new File("src/test/resources/versions.yaml")

        def options = new VersionResolverOptions()
        options.versionManifest = file.toURI().toString()

        def selectorMock = new MockFor(ModuleVersionSelector)
        selectorMock.demand.getVersion{ params-> '1.2.3'}

        def detailsMock = new MockFor(DependencyResolveDetails)
        detailsMock.demand.getRequested{params-> selectorMock.proxyInstance()}

        def resolver = new VersionResolver(null, options)
        def ver = resolver.resolveVersionFromManifest(detailsMock.proxyInstance())
        assert ver == "1.2.3"
    }
}