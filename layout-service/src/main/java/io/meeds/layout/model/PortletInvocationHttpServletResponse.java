/**
 * This file is part of the Meeds project (https://meeds.io/).
 *
 * Copyright (C) 2020 - 2024 Meeds Association contact@meeds.io
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package io.meeds.layout.model;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.WriteListener;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public class PortletInvocationHttpServletResponse implements HttpServletResponse {

  private PrintWriter           writer;

  private ByteArrayOutputStream buffer;

  private ServletOutputStream   outputStream;

  @Override
  public String getCharacterEncoding() {
    return null;
  }

  @Override
  public String getContentType() {
    return null;
  }

  @Override
  public ServletOutputStream getOutputStream() throws IOException {
    if (outputStream == null) {
      outputStream = new ServletOutputStream() {
        @Override
        public void write(int b) throws IOException {
          getBuffer().write(b);
        }

        @Override
        public boolean isReady() {
          return false;
        }

        @Override
        public void setWriteListener(WriteListener listener) {
          throw new UnsupportedOperationException();
        }
      };
    }
    return outputStream;
  }

  @Override
  public PrintWriter getWriter() throws IOException {
    if (writer == null) {
      writer = new PrintWriter(getBuffer());
    }
    return writer;
  }

  @Override
  public void setCharacterEncoding(String charset) {
    // NOP
  }

  @Override
  public void setContentLength(int len) {
    // NOP
  }

  @Override
  public void setContentLengthLong(long len) {
    // NOP
  }

  @Override
  public void setContentType(String type) {
    // NOP
  }

  @Override
  public void setBufferSize(int size) {
    // NOP
  }

  public ByteArrayOutputStream getBuffer() {
    if (buffer == null) {
      buffer = new ByteArrayOutputStream();
    }
    return buffer;
  }

  @Override
  public int getBufferSize() {
    return 0;
  }

  @Override
  public void flushBuffer() throws IOException {
    // NOP
  }

  @Override
  public void resetBuffer() {
    // NOP
  }

  @Override
  public boolean isCommitted() {
    return false;
  }

  @Override
  public void reset() {
    // NOP
  }

  @Override
  public void setLocale(Locale loc) {
    // NOP
  }

  @Override
  public Locale getLocale() {
    // NOP
    return null;
  }

  @Override
  public void addCookie(Cookie cookie) {
    // NOP
  }

  @Override
  public boolean containsHeader(String name) {
    return false;
  }

  @Override
  public String encodeURL(String url) {
    return url;
  }

  @Override
  public String encodeRedirectURL(String url) {
    return url;
  }

  @Override
  public void sendError(int sc, String msg) throws IOException {
    // NOP
  }

  @Override
  public void sendError(int sc) throws IOException {
    // NOP
  }

  @Override
  public void sendRedirect(String location) throws IOException {
    // NOP
  }

  @Override
  public void setDateHeader(String name, long date) {
    // NOP
  }

  @Override
  public void addDateHeader(String name, long date) {
    // NOP
  }

  @Override
  public void setHeader(String name, String value) {
    // NOP
  }

  @Override
  public void addHeader(String name, String value) {
    // NOP
  }

  @Override
  public void setIntHeader(String name, int value) {
    // NOP
  }

  @Override
  public void addIntHeader(String name, int value) {
    // NOP
  }

  @Override
  public void setStatus(int sc) {
    // NOP
  }

  @Override
  public int getStatus() {
    return 0;
  }

  @Override
  public String getHeader(String name) {
    return null;
  }

  @Override
  public Collection<String> getHeaders(String name) {
    return Collections.emptyList();
  }

  @Override
  public Collection<String> getHeaderNames() {
    return Collections.emptyList();
  }

}
