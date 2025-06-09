declare module 'keycloak-js' {
  interface KeycloakInstance {
    authenticated?: boolean;
    token?: string;
    refreshToken?: string;
    idToken?: string;
    login(options?: any): Promise<void>;
    logout(options?: any): Promise<void>;
    register(options?: any): Promise<void>;
    updateToken(minValidity?: number): Promise<boolean>;
    clearToken(): void;
    hasRealmRole(role: string): boolean;
    hasResourceRole(role: string, resource?: string): boolean;
    loadUserProfile(): Promise<any>;
    loadUserInfo(): Promise<any>;
  }

  interface KeycloakConfig {
    url?: string;
    realm: string;
    clientId: string;
  }

  interface KeycloakInitOptions {
    onLoad?: 'login-required' | 'check-sso';
    silentCheckSsoRedirectUri?: string;
    token?: string;
    refreshToken?: string;
    idToken?: string;
    checkLoginIframe?: boolean;
    checkLoginIframeInterval?: number;
    responseMode?: 'query' | 'fragment';
    flow?: 'standard' | 'implicit' | 'hybrid';
  }

  function Keycloak(config?: KeycloakConfig | string): KeycloakInstance;
  export = Keycloak;
}
